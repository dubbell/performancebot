package com.icetlab.performancebot.github;

import static org.mockito.Mockito.spy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GitHubWebhookHandlerTest {
  @InjectMocks
  private GitHubWebhookHandler payloadHandler;

  @BeforeEach
  public void setUp() {
    payloadHandler = spy(new GitHubWebhookHandler());
  }

  @Test
  public void testHandlePullRequestPayload() {
    // TODO: Make this match the new implementation.
  }

  @Test
  public void testHandleInstallationPayload() {
    // TODO: Implement this function, it is broken at the moment.
  }

}
