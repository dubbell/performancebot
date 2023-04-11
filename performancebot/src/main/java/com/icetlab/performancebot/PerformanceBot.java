package com.icetlab.performancebot;

import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.github.Issue;
import com.icetlab.performancebot.github.Payload;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * App serves the purpose of being the main entry for performancebot and hosts its REST API.
 */
@SpringBootApplication
@RestController
@EnableMongoRepositories
@EnableMongoAuditing
public class PerformanceBot {

  private static final Issue issue = new Issue();
  @Autowired
  private Payload payloadHandler;
  @Autowired
  private InstallationController database;

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
    return "Welcome to the performancebot.";
  }

  /**
   * POST route that listens for webhooks events sent by GitHub. Delegates work to other components
   * based on the received payload.
   *
   * @param payload JSON string with payload
   */
  @PostMapping(name = "/payload", value = "payload", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void payload(@RequestHeader(value = "X-Github-Event") String eventType,
      @RequestBody String payload) {
    payloadHandler.handlePayload(eventType, payload);
  }

  /**
   * POST route that listens for finished benchmark runs from the BenchmarkWorker and adds the
   * results to the database.
   *
   * @param payload JSON string with run results
   */
  @PostMapping("/benchmark")
  public void addRun(@RequestBody String payload) {
    System.out.println("Adding run results to database...");
    database.addRun(payload);
    payloadHandler.handleResults(payload);
  }

  public static Issue getIssue() {
    return issue;
  }
}
