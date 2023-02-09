package com.icetlab.performancebot;

import com.icetlab.performancebot.benchmark.IBenchmark;
import com.icetlab.performancebot.benchmark.JMHBenchmark;
import com.icetlab.performancebot.github.Issue;
import com.icetlab.performancebot.github.RepoCloner;
import com.icetlab.performancebot.stats.Analyzer;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.BasicJsonParser;
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
public class PerformanceBot {

  private static final IBenchmark benchmark = new JMHBenchmark();
  private static final RepoCloner repoCloner = new RepoCloner();
  private static final Issue issue = new Issue();
  private static final Analyzer analyzer = new Analyzer();
  private static final BasicJsonParser payloadParser = new BasicJsonParser();

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
  @PostMapping(name = "/payload", value = "payload",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public void payload(@RequestBody String payload) {
    Map<String, Object> map = payloadParser.parseMap(payload);
    if (map.containsKey("pull_request")) {
      // TODO: Handle pull requests.
    } else if (map.containsKey("installation")) {
      // TODO: Handle bot installation.
    }
  }
}
