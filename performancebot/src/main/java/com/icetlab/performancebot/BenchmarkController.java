package com.icetlab.performancebot;

import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.webhook.PayloadManager;
import java.util.Collections;
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
public class BenchmarkController {

  @Autowired
  private PayloadManager payloadHandler;
  @Autowired
  private InstallationController database;

  /**
   * The main entry of the application.
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BenchmarkController.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8080"));
    app.run(args);
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
    boolean handled;
    switch (eventType) {
      case "installation" -> handled = payloadHandler.handleInstall(payload);
      case "pull_request", "issue_comment" -> handled = payloadHandler.handlePullRequest(payload);
      case "installation_repositories" -> handled = payloadHandler.handleRepo(payload);
      default -> handled = false;
    }
    String statusMessage =
        String.format("%s event of type %s", handled ? "Ignored" : "Handled", eventType);
    System.out.println(statusMessage);
  }

  /**
   * POST route that listens for finished benchmark runs from the BenchmarkWorker and adds the
   * results to the database.
   *
   * @param payload JSON string with run results
   */
  @PostMapping("/benchmark")
  public void addRun(@RequestBody String payload) {
    database.addRun(payload);
    payloadHandler.handleResults(payload);
  }
}
