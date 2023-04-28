package com.icetlab.performancebot.github.webhook;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.webhook.handlers.PullRequestHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.ResourceAccessException;

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
    // It should crash here, since the connection to bworker does not exist.
    // This is the last thing that happens in the method, which means that we
    // have successfully reached the end where we are about to send instructinos
    // to bworker.
    Assertions.assertThrows(ResourceAccessException.class,
        () -> pullRequestHandler.handle(WebhookMocks.PR_WITH_PING));
  }
}
