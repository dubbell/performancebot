package com.icetlab.benchmark_worker;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
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
  @PostMapping(name = "/benchmark", value = "benchmark", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void benchmark(@RequestBody String task) {
    JacksonJsonParser parser = new JacksonJsonParser();

    String repoName = (String) parser.parseMap(task).get("name");
    String repoURL = (String) parser.parseMap(task).get("url");
    String accessToken = (String) parser.parseMap(task).get("token");

    cloneRepo(repoName, repoURL, accessToken);

    // TODO run benchmark
    // TODO send to database
    // TODO analysis
    // TODO post issue

    deleteRepo(repoName);
  }

  /**
   * Creates directory and clones repository into it.
   * @param repoName name of the repository
   * @param repoURL repository url
   * @param accessToken repository access token for authentication
   */
  private void cloneRepo(String repoName, String repoURL, String accessToken) {

    // creates directory
    File dir = new File(repoName);
    dir.mkdir();


    try {
      CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(accessToken, "");
      Git.cloneRepository()
          .setCredentialsProvider(credentials)
          .setURI(repoURL)
          .setDirectory(dir)
          .call();
    } catch (Exception e) {}

    // TODO error handling
  }

  /**
   * Deletes local clone of repository with the given name.
   * @param repoName name of the repository
   */
  private void deleteRepo(String repoName) {
    try {
      FileUtils.deleteDirectory(new File(repoName));
    } catch (IOException e) {}

    // TODO error handling
  }
}