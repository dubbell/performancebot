package com.icetlab.performancebot.github.webhook;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

@Component
public class PullRequestHandler extends WebhookHandler {
  private KubernetesClient kubernetesClient;

  /**
   * Handles the payload received from GitHub when a pull request event is received. If it does not
   * contain the ping <code>[performancebot]</code> in the message body or title, the request is
   * ignored.
   *
   * @param payload the payload received from GitHub
   */
  @Override
  public void handle(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    boolean pullRequestWasOpened = node.get("action").asText().equals("opened");
    boolean pullRequestReceivedComment = !node.get("issue").isNull();
    if (!pullRequestWasOpened && !pullRequestReceivedComment) {
      return;
    }

    if (!containsPing(node, pullRequestReceivedComment)) {
      return;
    }

    Map<String, Object> requestBody =
        createRequestBodyForBenchmarkWorker(node, pullRequestReceivedComment);

    sendRequestToBenchmarkWorker(requestBody);
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
    String repoUrl = pullRequestReceivedComment ? node.get("repository").get("clone_url").asText()
        : node.get("pull_request").get("head").get("repo").get("clone_url").asText();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("url", repoUrl);
    requestBody.put("token", "");
    requestBody.put("installation_id", installationId);
    requestBody.put("repo_id", repoId);
    requestBody.put("issue_url", issuesUrl);
    requestBody.put("name", name);

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

    String containerIp = "http://" + getWorkerServiceAddress();
    System.out.println("Sending request to " + containerIp + "/task");
    restTemplate.postForEntity(URI.create(containerIp + "/task"), requestEntity, String.class);
  }

  /**
   * Finds the ip and port of the benchmark-worker kubernetes service.
   */
  private String getWorkerServiceAddress() {
    if (kubernetesClient == null)
      kubernetesClient = new KubernetesClientBuilder().build();

    Service service = kubernetesClient.services().withName("benchmark-worker-svc").get();
    int port = service.getSpec().getPorts().get(0).getNodePort();
    String ip = kubernetesClient.nodes().list().getItems().get(0).getStatus().getAddresses().get(0)
        .getAddress();
    return ip + ":" + port;
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
