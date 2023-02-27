package com.icetlab.performancebot.github;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PayloadTest {
  @InjectMocks
  private Payload payloadHandler;

  @BeforeEach
  public void setUp() {
    payloadHandler = spy(new Payload());
  }

  @Test
  public void testHandlePullRequestPayload() {
    String payload = "{\n" + "  \"action\": \"opened\",\n" + "  \"number\": 2,\n"
        + "  \"pull_request\": {\n" + "    \"id\": 34778301,\n"
        + "    \"issue_url\": \"https://api.github.com/repos/ongod/steezy/issues/2\"\n" + "  },\n"
        + "  \"installation\": {\n" + "    \"id\": 1\n" + "  }\n" + "}";

    // Ugly try catch block to avoid exception
    try {
      payloadHandler.handlePayload(payload);
    } catch (Exception e) {

    }
    verify(payloadHandler, times(1)).handlePullRequest(payload);
    verify(payloadHandler, never()).handleNewInstall(payload);
  }

  @Test
  public void testHandleInstallationPayload() {
    // TODO: Implement this function, it is broken at the moment.
  }

}
