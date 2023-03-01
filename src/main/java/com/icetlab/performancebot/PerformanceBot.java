package com.icetlab.performancebot;

import com.icetlab.performancebot.benchmark.IBenchmark;
import com.icetlab.performancebot.benchmark.JMHBenchmark;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.service.InstallationService;
import com.icetlab.performancebot.github.Issue;
import com.icetlab.performancebot.github.Payload;
import com.icetlab.performancebot.github.RepoCloner;
import com.icetlab.performancebot.stats.Analyzer;
import java.util.ArrayList;
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
  private static final RepoCloner repoCloner = new RepoCloner();
  private static final Issue issue = new Issue();
  @Autowired
  private Payload payloadHandler;
  private static final Analyzer analyzer = new Analyzer();
  public static final JsonParser payloadParser = new JacksonJsonParser();
  @Autowired
  private InstallationService serv;

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
    // serv.addRepoToInstallation("123", new GitHubRepo("test", new HashSet<>()));
    // serv.addMethodToRepo("123", "test", new Method("testMethod", new ArrayList<>()));
    serv.addRunResultToMethod("123", "test", "testMethod", "holy moly");
    return "Welcome to the performancebot.";
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

  public static Issue getIssue() {
    return issue;
  }
}

