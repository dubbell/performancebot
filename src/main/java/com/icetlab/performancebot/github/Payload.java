package com.icetlab.performancebot.github;

import static com.icetlab.performancebot.PerformanceBot.getIssue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JacksonJsonParser;

/**
 * Handles the payload received from GitHub.
 */
public class Payload {

  private final JacksonJsonParser payloadParser;

  public Payload() {
    payloadParser = new JacksonJsonParser();
  }

  /**
   * Handles the payload received from GitHub.
   *
   * @param payload the payload received from GitHub
   */
  public void handlePayload(String payload) {
    boolean isPullRequest = payloadParser.parseMap(payload).get("pull_request") != null;
    /*
     * FIXME: This is not a reliable way of checking if the payload is a new installation. We need
     * to look in the database to see if the installation is already there.
     */
    boolean isNewInstall = payloadParser.parseMap(payload).get("action").equals("created");
    if (isPullRequest) {
      handlePullRequest(payload);
    } else if (isNewInstall) {
      handleNewInstall(payload);
    }
  }

  /**
   * Handles the payload received from GitHub when a new installation is created.
   *
   * @param payload the payload received from GitHub
   */
  void handleNewInstall(String payload) {
    System.out.println("New installation created. I do nothing, though.");
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
    getIssue().createIssue(issuesUrl, "hello", "my name is performancebot", installationId);
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
