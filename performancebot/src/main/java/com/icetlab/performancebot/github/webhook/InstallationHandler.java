package com.icetlab.performancebot.github.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.icetlab.performancebot.database.controller.InstallationController;

@Component
public class InstallationHandler extends WebhookHandler {
  @Autowired
  private InstallationController database;

  /**
   * Handles the payload received from GitHub when a new installation is created.
   *
   * @param payload the payload received from GitHub
   */
  @Override
  public boolean handle(String payload) {
    JsonNode node = getPayloadAsNode(payload);
    boolean isNewInstall = node.get("action").asText().equals("created");
    String installationId = node.get("installation").get("id").asText();
    if (installationId == null)
      return false;
    if (!isNewInstall) {
      database.deleteInstallation(installationId);
      return true;
    }

    database.addInstallation(installationId);
    return true;
  }

}
