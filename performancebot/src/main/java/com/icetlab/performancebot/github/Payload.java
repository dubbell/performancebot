package com.icetlab.performancebot.github;

import static com.icetlab.performancebot.PerformanceBot.getIssue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.stats.GitHubIssueFormatter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Payload {

  @Autowired
  private GitHubIssueFormatter gitHubIssueFormatter;

  /**
   * Handles the payload received from GitHub. Depending on the payload, it either adds a new
   * installation or reads information about a newly opened pull request.
   *
   * @param payload the payload received from GitHub
   */
  public void handlePayload(String eventType, String payload) {
    switch (eventType) {
      case "installation" -> handleNewInstall(payload);
      case "pull_request" -> handlePullRequest(payload);
      default -> System.out.println("Received unsupported event type: " + eventType);
    }
  }

  /**
   * Handles the payload received from GitHub when a new installation is created.
   *
   * @param payload the payload received from GitHub
   */
  void handleNewInstall(String payload) {}

  /**
   * Handles the payload received from GitHub when a pull request is opened.
   *
   * @param payload the payload received from GitHub
   */
  void handlePullRequest(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    String installationId = node.get("installation").get("id").asText();
    String issuesUrl = node.get("pull_request").get("issue_url").asText();
    String repoId = node.get("repository").get("id").asText();
    String name = node.get("repository").get("name").asText();
    issuesUrl = issuesUrl.substring(0, issuesUrl.lastIndexOf("/"));
    String repoUrl = node.get("pull_request").get("head").get("repo").get("clone_url").asText();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("url", repoUrl);
    requestBody.put("token", "");
    requestBody.put("installation_id", installationId);
    requestBody.put("repo_id", repoId);
    requestBody.put("issue_url", issuesUrl);
    requestBody.put("name", name);

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
    RestTemplate restTemplate = new RestTemplate();

    String containerIp = "http://" + getWorkerServiceAddress();
    System.out.println("Sending request to " + containerIp + "/task");
    restTemplate.postForEntity(URI.create(containerIp + "/task"), requestEntity, String.class);
  }

  private KubernetesClient kubernetesClient;

  /**
   * Finds the ip and port of the benchmark-worker kubernetes service.
   */
  private String getWorkerServiceAddress() {
    if (kubernetesClient == null)
      kubernetesClient = new KubernetesClientBuilder().build();

    Service service = kubernetesClient.services().withName("benchmark-worker-svc").get();
    int port = service.getSpec().getPorts().get(0).getNodePort();
    String ip = kubernetesClient.nodes().list().getItems().get(0).getStatus().getAddresses().get(0).getAddress();
    return ip + ":" + port;
  }

  /**
   * Handles the payload received from GitHub when the results of a performance test are ready.
   * 
   * @param payload the payload received from BenchmarkWorker
   */
  public void handleResults(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    String installationId = node.get("installation_id").asText();
    String issueUrl = node.get("issue_url").asText();
    String name = node.get("name").asText();
    String formattedResults = gitHubIssueFormatter.formatBenchmarkIssue(payload);
    getIssue().createIssue(issueUrl, "Results for " + name, formattedResults, installationId);
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
