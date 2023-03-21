package com.icetlab.performancebot.github;

import static com.icetlab.performancebot.PerformanceBot.getIssue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.stats.IssueLogger;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

@Component
public class Payload {

  private final JacksonJsonParser payloadParser = new JacksonJsonParser();

  /**
   * Handles the payload received from GitHub.
   *
   * @param payload the payload received from GitHub
   */
  public void handlePayload(String payload) {
    boolean isPullRequest = payloadParser.parseMap(payload).containsKey("pull_request")
        && payloadParser.parseMap(payload).containsKey("action");

    if (isPullRequest) {
      handlePullRequest(payload);
    }
  }

  /**
   * Handles the payload received from GitHub when a new installation is created.
   *
   * @param payload the payload received from GitHub
   */
  void handleNewInstall(String payload) {
  }

  /**
   * Handles the payload received from GitHub when a pull request is opened.
   *
   * @param payload the payload received from GitHub
   */
  void handlePullRequest(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    String installationId = node.get("installation").get("id").asText();
    String issuesUrl = node.get("pull_request").get("issue_url").asText();

    // This url is used to clone the repo.
    // TODO: Handle the repo to clone in a container, feel free to do whatever you
    // want, this just how to obtain it.
    String repoUrl = node.get("pull_request").get("head").get("repo").get("clone_url").asText();

    issuesUrl = issuesUrl.substring(0, issuesUrl.lastIndexOf("/"));

    try {
      // FIXME: Path should not be hardcoded
      String userdir = System.getProperty("user.dir");
      String path = userdir + "/src/main/java/com/icetlab/performancebot/stats/jmh-result.json";
      String json = new String(Files.readAllBytes(Paths.get(path)));
      String issueText = IssueLogger.createSimpleIssue(json);

      getIssue().createIssue(issuesUrl, "TODO: dummy title", issueText, installationId);

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Converts the payload received from GitHub to a JsonNode.
   *
   * @param payload the payload received from GitHub
   * @return the payload as a JsonNode
   */
  private JsonNode getPayloadAsNode(String payload) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readTree(payload);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid JSON payload: " + payload, e);
    }
  }
}
