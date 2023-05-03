package com.icetlab.benchmarkworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.benchmarkworker.client.Kubernetes;
import com.icetlab.benchmarkworker.client.PerformanceBotClient;
import com.icetlab.benchmarkworker.client.Localhost;
import com.icetlab.benchmarkworker.configuration.Configuration;
import com.icetlab.benchmarkworker.configuration.ConfigurationFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Spring boot application to be run in containers.
 *
 * Is given tasks to complete by the performancebot, which consists of: 1.
 * Cloning the given repository into a local directory. 2. Compiling and running
 * all specified benchmarks in the cloned repository. 3. Sending the results
 * back to the performancebot.
 */
@RestController
@SpringBootApplication
public class BenchmarkWorker {

  Logger logger = LoggerFactory.getLogger(BenchmarkWorker.class);
  PerformanceBotClient client =
      new Kubernetes(); // Change to Kubernetes() to run on kubernetes

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BenchmarkWorker.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
    app.run(args);
  }

  /**
   * Listens for new tasks from the performancebot.
   */
  @PostMapping(name = "/task", value = "task",
               consumes = MediaType.APPLICATION_JSON_VALUE)
  public void
  startTask(@RequestBody String task) {
    JacksonJsonParser parser = new JacksonJsonParser();

    String repoURL = (String)parser.parseMap(task).get("url");
    String accessToken = (String)parser.parseMap(task).get("token");
    String branch = (String)parser.parseMap(task).get("branch");

    String results = "";

    // if one thing fails, the benchmark is cancelled
    try {
      // clone repo
      clone(repoURL, accessToken, branch);

      // reads configuration from .yaml file
      Configuration configuration = ConfigurationFactory.getConfiguration();

      // compile and get result of benchmark
      results = configuration.benchmark(); // saves result to json file

      System.out.println(results);
    } catch (Exception e) {
      logger.error(e.toString());
    }

    // send result back to the performance bot
    // if benchmark failed, then result is just an empty string
    try {
      sendResult(results,
              parser.parseMap(task).get("installation_id").toString(),
              parser.parseMap(task).get("repo_id").toString(),
              parser.parseMap(task).get("name").toString(),
              parser.parseMap(task).get("issue_url").toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    // delete local installation of repository
    delete();
  }

  /**
   * Creates directory and clones repository into it.
   *
   * @param repoURL repository url
   * @param accessToken repository access token for authentication
   */
  public void clone(String repoURL, String accessToken, String branch)
      throws Exception {
    System.out.println("Cloning started.");

    // creates directory
    File dir = new File("benchmark_directory");
    if (!dir.mkdir()) // attempts to create directory
      return;

    CredentialsProvider credentials =
        new UsernamePasswordCredentialsProvider(accessToken, "");
    Git.cloneRepository()
        .setCredentialsProvider(credentials) // if the repository is private,
                                             // the access token should
                                             // authorize the request
        .setURI(repoURL)
        .setDirectory(dir)
        .setBranch(branch)
        .call()
        .close();

    System.out.println("Cloning finished.");
  }

  /**
   * Sends results back to benchmark-controller process.
   */
  public void sendResult(String results, String installationId, String repoId,
                         String name, String endpoint) throws Exception {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("installation_id", installationId);
    requestBody.put("repo_id", repoId);
    requestBody.put("name", name);
    requestBody.put("issue_url", endpoint);

    // if an error occurred and a result wasn't calculated, don't add the results to the body
    if (results.equals("")) {
      ObjectMapper mapper = new ObjectMapper();
      Object[] resultList = mapper.readValue(results.trim(), Object[].class);
      requestBody.put("results", resultList);
    }

    HttpEntity<Map<String, Object>> requestEntity =
        new HttpEntity<>(requestBody);
    RestTemplate restTemplate = new RestTemplate();

    restTemplate.postForEntity(
        URI.create(client.getServerIpWithPort() + "/benchmark"), requestEntity,
        String.class);
  }

  /**
   * Deletes local clone of repository with the given name.
   */
  public void delete() {
    try {
      FileUtils.deleteDirectory(new File("benchmark_directory"));
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
