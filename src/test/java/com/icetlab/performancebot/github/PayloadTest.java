package com.icetlab.performancebot.github;

import static org.mockito.Mockito.atLeastOnce;
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
    // TODO: Make this match the new implementation.
  }

  @Test
  public void testHandleInstallationPayload() {
    // TODO: Implement this function, it is broken at the moment.
  }

}
