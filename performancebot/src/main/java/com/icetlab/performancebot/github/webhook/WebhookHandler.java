package com.icetlab.performancebot.github.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class WebhookHandler {
  abstract void handle(String payload);

  /**
   * Converts the payload received from GitHub to a JsonNode.
   *
   * @param payload the payload received from GitHub
   * @return the payload as a JsonNode
   */
  JsonNode getPayloadAsNode(String payload) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readTree(payload);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid JSON payload: " + payload, e);
    }
  }
}
