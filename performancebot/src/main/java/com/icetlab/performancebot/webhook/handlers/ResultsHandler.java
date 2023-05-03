package com.icetlab.performancebot.webhook.handlers;

import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.icetlab.performancebot.github.GitHubIssueManager;
import com.icetlab.performancebot.stats.BarPlotIssueFormatter;

@Component
public class ResultsHandler extends WebhookHandler {
  @Autowired
  BarPlotIssueFormatter issueFormatter;

  /**
   * Handles the payload received from GitHub when the results of a performance test are ready.
   * 
   * @param payload the payload received from BenchmarkWorker
   */
  @Override
  public boolean handle(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    String installationId = node.get("installation_id").asText();
    String issueUrl = node.get("issue_url").asText();
    String name = node.get("name").asText();
    JsonNode results = node.get("results");
    if (results == null)
      return false;
    if (Stream.of(installationId, issueUrl, name).anyMatch(Objects::isNull))
      return false;
    String formattedResults = issueFormatter.formatBenchmarkIssue(payload);
    GitHubIssueManager.getInstance().createIssue(issueUrl, "Results for " + name, formattedResults,
        installationId);
    return true;
  }

}
