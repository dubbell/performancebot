package com.icetlab.performancebot;

import com.icetlab.performancebot.benchmark.IBenchmark;
import com.icetlab.performancebot.benchmark.JMHBenchmark;
import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.github.Issue;
import com.icetlab.performancebot.github.Payload;
import com.icetlab.performancebot.stats.Analyzer;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * App serves the purpose of being the main entry for performancebot and hosts its REST API.
 */
@SpringBootApplication
@RestController
@EnableMongoRepositories
public class PerformanceBot {

  private static final IBenchmark benchmark = new JMHBenchmark();
  private static final Issue issue = new Issue();
  @Autowired
  private Payload payloadHandler;
  private static final Analyzer analyzer = new Analyzer();
  public static final JsonParser payloadParser = new JacksonJsonParser();
  @Autowired
  private InstallationController installationController;

  /**
   * The main entry of the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(PerformanceBot.class, args);
  }

  /**
   * GET "/", simply responds with a welcoming message to the caller.
   *
   * @return a welcome message
   */
  @GetMapping("/")
  public String root() {
    //installationController.addInstallation("33697560");
    //installationController.addRepoToInstallation("33697560", new GitHubRepo("608560819", new HashSet<>(), "jmh"));

    return "Welcome to the performancebot. ";
  }

  /**
   * POST route that listens for webhooks events sent by GitHub. Delegates work to other components
   * based on the received payload.
   *
   * @param payload JSON string with payload
   */
  @PostMapping(name = "/payload", value = "payload", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void payload(@RequestBody String payload) {
    payloadHandler.handlePayload(payload);
  }

  /**
   * POST route that listens for finished benchmark runs and adds the results to the database.
   *
   * @param payload JSON string with payload
   */
  @PostMapping("/benchmark")
  public void addRun(@RequestBody String payload) {
    installationController.addRun(payload);
  }

  public static Issue getIssue() {
    return issue;
  }
}

