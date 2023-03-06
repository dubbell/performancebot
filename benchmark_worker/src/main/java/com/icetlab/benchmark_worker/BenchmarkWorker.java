package com.icetlab.benchmark_worker;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icetlab.benchmark_worker.configuration.ConfigData;
import com.icetlab.benchmark_worker.configuration.Configuration;
import com.icetlab.benchmark_worker.configuration.MavenConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;

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

 * Is given tasks to complete by the performancebot, which consists of:
 * 1. Cloning the given repository into a local directory.
 * 2. Compiling and running all specified benchmarks in the cloned repository.
 * 3. Sending the results to the database.
 * 4. Performing statistical analysis.
 * 5. Sending the result of the analysis to the remote repository as a GitHub Issue.
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
      Configuration configuration = getConfiguration();

      // compile and get result of benchmark
      String result = configuration.benchmark(); // saves result to json file

      // send result back to the performance bot
      sendResult(result, request.getRemoteAddr());
    }
    catch (Exception e) {
      logger.error(e.toString());
    }

    // delete local installation of repository
    delete();
  }

  /**
   * Creates directory and clones repository into it.
   * @param repoURL repository url
   * @param accessToken repository access token for authentication
   */
  public void clone(String repoURL, String accessToken) throws Exception {

    // creates directory
    File dir = new File("benchmark_directory");
    if (!dir.mkdir()) // attempts to create directory
      return;

    CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(accessToken, "");
    Git.cloneRepository()
      .setCredentialsProvider(credentials) // if the repository is private, the access token should authorize the request
      .setURI(repoURL)
      .setDirectory(dir)
      .call().close();
  }

  /**
   * Gets project configuration from .yaml file.
   */
  public Configuration getConfiguration() throws Exception {
    ConfigData configData = new ObjectMapper(new YAMLFactory()).readValue(new File("benchmark_directory/perfbot.yaml"), ConfigData.class);
    if (configData.getLanguage().equalsIgnoreCase("java")) {
      if (configData.getBuildTool().equalsIgnoreCase("maven")) {
        return new MavenConfiguration();
      }
      else {
        throw new Exception("Invalid project configuration for the performance bot: invalid build tool.");
      }
    }
    else {
      throw new Exception("Invalid project configuration for the performance bot: invalid language.");
    }
  }

  /**
   * Sends result of benchmark back to the performance bot.
   */
  public void sendResult(String result, String senderURI) throws HttpClientErrorException {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("body", result);

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, new HttpHeaders());
    RestTemplate restTemplate = new RestTemplate();

    restTemplate.postForEntity(URI.create(senderURI + "/benchmark"), requestEntity, String.class);
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