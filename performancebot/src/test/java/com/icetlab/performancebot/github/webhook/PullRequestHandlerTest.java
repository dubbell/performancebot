package com.icetlab.performancebot.github.webhook;

import com.icetlab.performancebot.database.model.Installation;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class PullRequestHandlerTest {

  @InjectMocks
  @Autowired
  PullRequestHandler pullRequestHandler;

  @Autowired
  MongoTemplate mongoTemplate;

  @AfterEach
  public void tearDown() {
    mongoTemplate.dropCollection(Installation.class);
  }

  @Test
  public void testHandlePullRequestWithoutPing() {
    Assertions.assertFalse(pullRequestHandler.handle(WebhookMocks.PR_WITHOUT_PING));
  }

  @Test
  public void testHandlePullRequestWithPingButActionIsClosed() {
    Assertions.assertFalse(pullRequestHandler.handle(WebhookMocks.CLOSED_PR_WITH_PING));
  }

  @Test
  public void testHandlePullRequestWithPing() {
    // If we get to the handling part, we are dependent on a working k8s connection.
    // If we get there, it means all checks have passed!
    Assertions.assertThrows(KubernetesClientException.class,
      () -> pullRequestHandler.handle(WebhookMocks.PR_WITH_PING));
  }
}
