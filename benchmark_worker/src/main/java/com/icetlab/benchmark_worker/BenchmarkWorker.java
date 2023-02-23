package com.icetlab.benchmark_worker;

import java.io.IOException;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;


/**
 * Spring boot application to be run in containers.

 * Is given tasks to complete by the performancebot, which consists of:
 * 1. Cloning the given repository into a local directory.
 * 2. Compiling and running all specified benchmarks in the cloned repository.
 * 3. Sending the results to the database.
 * 4. Performing statistical analysis.
 * 5. Sending the result of the analysis to the remote repository as a Github Issue.
 */
@SpringBootApplication
public class BenchmarkWorker {

  public static void main(String[] args) {
    SpringApplication.run(BenchmarkWorker.class, args);
  }

  /**
   * Listens for new tasks from the performancebot.
   */
  @PostMapping(name = "/task", value = "task", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void startTask(@RequestBody String task) {
    JacksonJsonParser parser = new JacksonJsonParser();

    String repoURL = (String) parser.parseMap(task).get("url");
    String accessToken = (String) parser.parseMap(task).get("token");

    // if one thing fails, the benchmark is cancelled
    try {
      clone(repoURL, accessToken);
      compile();

      // TODO run benchmark
      // TODO send to database
      // TODO analysis
      // TODO post issue
    }
    catch (Exception e) {}

    delete();
  }

  /**
   * Creates directory and clones repository into it.
   * @param repoURL repository url
   * @param accessToken repository access token for authentication
   */
  private void clone(String repoURL, String accessToken) throws Exception {

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
  private void compile() throws Exception {
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

  private void benchmark() throws Exception {

  }

  /**
   * Deletes local clone of repository with the given name.
   */
  private void delete() {
    try {
      FileUtils.deleteDirectory(new File("benchmark_directory"));
    } catch (IOException ignored) {}

    // TODO error handling?
  }
}