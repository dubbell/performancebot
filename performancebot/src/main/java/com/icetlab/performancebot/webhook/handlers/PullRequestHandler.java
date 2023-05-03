package com.icetlab.performancebot.webhook.handlers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.icetlab.performancebot.client.Kubernetes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.icetlab.performancebot.client.BenchmarkWorkerClient;
import com.icetlab.performancebot.client.Localhost;

@Component
public class PullRequestHandler extends WebhookHandler {

  private BenchmarkWorkerClient benchmarkWorkerClient = new Kubernetes();

  /**
   * Handles the payload received from GitHub when a pull request event is received. If it does not
   * contain the ping <code>[performancebot]</code> in the message body or title, the request is
   * ignored.
   *
   * @param payload the payload received from GitHub
   */
  @Override
  public boolean handle(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    boolean pullRequestWasOpened = node.get("action").asText().endsWith("opened");
    boolean pullRequestReceivedComment = node.get("issue") != null && !node.get("issue").isNull();
    if (!pullRequestWasOpened && !pullRequestReceivedComment) {
      return false;
    }

    if (!containsPing(node, pullRequestReceivedComment)) {
      return false;
    }

    Map<String, Object> requestBody =
        createRequestBodyForBenchmarkWorker(node, pullRequestReceivedComment);
    if (requestBody.isEmpty())
      return false;

    sendRequestToBenchmarkWorker(requestBody);
    return true;
  }

  /**
   * Builds a request body for the benchmark-worker service.
   *
   * @param node the payload received from GitHub
   * @param pullRequestReceivedComment whether the pull request received a comment
   * @return the request body
   */
  private Map<String, Object> createRequestBodyForBenchmarkWorker(JsonNode node,
      boolean pullRequestReceivedComment) {
    String installationId = node.get("installation").get("id").asText();
    String issuesUrl = pullRequestReceivedComment ? node.get("issue").get("url").asText()
        : node.get("pull_request").get("issue_url").asText();
    String repoId = node.get("repository").get("id").asText();
    String name = node.get("repository").get("name").asText();
    issuesUrl = issuesUrl.substring(0, issuesUrl.lastIndexOf("/"));
    String repoUrl = node.get("repository").get("clone_url").asText();
    String branch = node.get("pull_request").get("head").get("ref").asText();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("url", repoUrl);
    requestBody.put("token", "");
    requestBody.put("installation_id", installationId);
    requestBody.put("repo_id", repoId);
    requestBody.put("issue_url", issuesUrl);
    requestBody.put("name", name);
    requestBody.put("branch", branch);

    return requestBody;
  }

  /**
   * Sends a request to the benchmark-worker service with the given request body.
   *
   * @param requestBody the request body to send
   */
  private void sendRequestToBenchmarkWorker(Map<String, Object> requestBody) {
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
    RestTemplate restTemplate = new RestTemplate();

    String containerIp = "http://" + benchmarkWorkerClient.getServerIp() + "/task";
    System.out.println("Sending request to " + containerIp);
    restTemplate.postForEntity(URI.create(containerIp), requestEntity, String.class);
  }

  /**
   * Checks if the pull request contains the ping <code>[performancebot]</code> in the message body
   *
   * @param node
   * @param pullRequestReceivedComment
   * @return true if the ping is found, false otherwise
   */
  private boolean containsPing(JsonNode node, boolean pullRequestReceivedComment) {
    String ping = "[performancebot]";
    if (pullRequestReceivedComment) {
      String comment = node.get("comment").get("body").asText();
      return comment.toLowerCase().contains(ping);
    } else {
      boolean pullRequestBodyContainsPing =
          node.get("pull_request").get("body").asText().toLowerCase().contains(ping);
      boolean pullRequestTitleContainsPing =
          node.get("pull_request").get("title").asText().toLowerCase().contains(ping);
      return pullRequestBodyContainsPing || pullRequestTitleContainsPing;
    }
  }
}
