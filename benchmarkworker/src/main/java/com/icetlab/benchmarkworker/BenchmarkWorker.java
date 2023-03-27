package com.icetlab.benchmarkworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.benchmarkworker.configuration.Configuration;
import com.icetlab.benchmarkworker.configuration.ConfigurationFactory;
import java.io.IOException;
import java.net.URI;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.commons.io.FileUtils;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Spring boot application to be run in containers.
 * 
 * Is given tasks to complete by the performancebot, which consists of: 1. Cloning the given
 * repository into a local directory. 2. Compiling and running all specified benchmarks in the
 * cloned repository. 3. Sending the results to the database. 4. Performing statistical analysis. 5.
 * Sending the result of the analysis to the remote repository as a GitHub Issue.
 */
@RestController
@SpringBootApplication
public class BenchmarkWorker {

  Logger logger = LoggerFactory.getLogger(BenchmarkWorker.class);

  public static void main(String[] args) {
    SpringApplication.run(BenchmarkWorker.class, args);
  }

  /**
   * Listens for new tasks from the performancebot.
   */
  @PostMapping(name = "/task", value = "task", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void startTask(@RequestBody String task, HttpServletRequest request) {
    JacksonJsonParser parser = new JacksonJsonParser();

    String repoURL = (String) parser.parseMap(task).get("url");
    String accessToken = (String) parser.parseMap(task).get("token");

    // if one thing fails, the benchmark is cancelled
    try {
      // clone repo
      clone(repoURL, accessToken);

      // reads configuration from .yaml file
      Configuration configuration = ConfigurationFactory.getConfiguration();

      // compile and get result of benchmark
      String result = configuration.benchmark(); // saves result to json file

      // send result back to the performance bot
      compile();
      benchmark(); // saves result to json file
      sendResult(readResults(), request.getRemoteAddr(),
          parser.parseMap(task).get("installation_id").toString(),
          parser.parseMap(task).get("repo_id").toString(),
          parser.parseMap(task).get("name").toString(),
          parser.parseMap(task).get("issue_url").toString());
    } catch (Exception e) {
      logger.error(e.toString());
      logger.error(request.getRemoteAddr());
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
  public void clone(String repoURL, String accessToken) throws Exception {
    System.out.println("Cloning started.");

    // creates directory
    File dir = new File("benchmark_directory");
    if (!dir.mkdir()) // attempts to create directory
      return;

    CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(accessToken, "");
    Git.cloneRepository().setCredentialsProvider(credentials) // if the repository is private, the
                                                              // access token should authorize the
                                                              // request
        .setURI(repoURL).setDirectory(dir).call().close();

    System.out.println("Cloning finished.");
  }

  /**
   * Sends result of benchmark back to the performance bot.
   */
  public void compile() throws Exception {
    // construct request to clean target directory
    InvocationRequest cleanRequest = new DefaultInvocationRequest();
    cleanRequest.setPomFile(new File("benchmark_directory/pom.xml"));
    cleanRequest.setGoals(Collections.singletonList("clean"));

    // construct request to compile project
    InvocationRequest verifyRequest = new DefaultInvocationRequest();
    verifyRequest.setPomFile(new File("benchmark_directory/pom.xml"));
    verifyRequest.setGoals(Collections.singletonList("verify"));

    // cleans and then compiles project
    Invoker invoker = new DefaultInvoker();
    if (System.getProperty("os.name").contains("Windows")) // only set maven home if on windows
      invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));

    InvocationResult cleanResult = invoker.execute(cleanRequest);
    InvocationResult verifyResult = invoker.execute(verifyRequest);

    // checks if either of the requests failed
    if (cleanResult.getExitCode() != 0 || verifyResult.getExitCode() != 0)
      throw new Exception("Build failed.");
  }

  /**
   * Runs benchmarks in compiled project and stores the result in a json file.
   */
  public void benchmark() throws Exception {
    Runtime.getRuntime().exec("java -jar ./benchmark_directory/target/benchmarks.jar -rf json")
        .waitFor();
  }

  public String readResults() throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get("jmh-result.json"));
    return new String(encoded, StandardCharsets.UTF_8);
  }

  public void sendResult(String results, String senderURI, String installationId, String repoId,
      String name, String endpoint) throws Exception {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("installation_id", installationId);
    requestBody.put("repo_id", repoId);
    requestBody.put("name", name);
    requestBody.put("issue_url", endpoint);

    ObjectMapper mapper = new ObjectMapper();
    Object[] resultList = mapper.readValue(results.trim(), Object[].class);
    requestBody.put("results", resultList);

    HttpEntity<Map<String, Object>> requestEntity =
        new HttpEntity<>(requestBody, new HttpHeaders());
    RestTemplate restTemplate = new RestTemplate();

    restTemplate.postForEntity(URI.create("http://" + senderURI + ":1337/benchmark"), requestEntity,
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
