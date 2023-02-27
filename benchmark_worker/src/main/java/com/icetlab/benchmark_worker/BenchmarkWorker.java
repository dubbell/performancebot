package com.icetlab.benchmark_worker;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvoker;
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

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
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
 * 5. Sending the result of the analysis to the remote repository as a Github Issue.
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
      clone(repoURL, accessToken);
      compile();
      benchmark(); // saves result to json file
      sendResult(readResults(), request.getRemoteAddr());
    }
    catch (Exception e) {
      logger.error(e.toString());
    }

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
      return; // TODO error handling?

    CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(accessToken, "");
    Git.cloneRepository()
      .setCredentialsProvider(credentials) // if the repository is private, the access token should authorize the request
      .setURI(repoURL)
      .setDirectory(dir)
      .call();
  }

  /**
   * Compiles the cloned repository by executing a new Maven build to the 'verify' phase.
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
    Runtime.getRuntime().exec("java -jar ./benchmark_directory/target/benchmarks.jar -rf json").waitFor();
  }

  public String readResults() throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get("jmh-result.json"));
    return new String(encoded, StandardCharsets.UTF_8);
  }

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
      new File("jmh-result.json").delete();
    } catch (IOException ignored) {}

    // TODO error handling?
  }
}