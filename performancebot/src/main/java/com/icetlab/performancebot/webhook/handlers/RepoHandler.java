package com.icetlab.performancebot.webhook.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.icetlab.performancebot.database.controller.InstallationController;

@Component
public class RepoHandler extends WebhookHandler {
  @Autowired
  private InstallationController db;

  @Override
  public boolean handle(String payload) {
    JsonNode jsonPayload = getPayloadAsNode(payload);
    boolean removed = isRemoveEvent(jsonPayload);
    if (!removed) {
      return false;
    }

    if (!payloadIsValid(jsonPayload))
      return false;

    boolean success = true;
    String installationId = jsonPayload.get("installation").get("id").asText();
    for (JsonNode repo : jsonPayload.get("repositories_removed")) {
      System.out.println(repo.toPrettyString());
      success = db.deleteGitHubRepo(installationId, repo.get("id").asText());
      if (!success)
        return false;
    }
    return true;
  }

  private boolean isRemoveEvent(JsonNode jsonPayload) {
    return jsonPayload.get("action").asText().equals("removed");
  }

  private boolean payloadIsValid(JsonNode jsonPayload) {
    JsonNode installation = jsonPayload.get("installation");
    return installation != null && installation.get("id") != null
        && jsonPayload.get("repositories_removed") != null;
  }

}
