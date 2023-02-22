package com.icetlab.benchmark_worker;


import com.icetlab.performancebot.PerformanceBot;
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
    SpringApplication.run(PerformanceBot.class, args);
  }

  /**
   * Listens for new tasks from the performancebot.
   */
  @PostMapping(name = "/benchmark", value = "benchmark", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void benchmark(@RequestBody String task) {
    JacksonJsonParser parser = new JacksonJsonParser();

    String repoURL = (String) parser.parseMap(task).get("url");
    String accessToken = (String) parser.parseMap(task).get("token");

    


  }
}