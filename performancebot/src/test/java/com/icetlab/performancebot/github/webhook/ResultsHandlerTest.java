package com.icetlab.performancebot.github.webhook;

import static org.junit.Assert.assertThrows;

import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

@SpringBootTest
public class ResultsHandlerTest {

  @InjectMocks
  @Autowired
  private ResultsHandler resultsHandler;

  @InjectMocks
  @Autowired
  private InstallationController installationController;

  @Autowired
  MongoTemplate mongoTemplate;

  boolean status;

  @AfterEach
  public void tearDown() {
    mongoTemplate.dropCollection(Installation.class);
  }

  @Test
  public void testHandle() {
    installationController.addInstallation("an id");
    installationController.addRepoToInstallation("an id",
      new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    // Since we do not want to expose an API key, we will get an unauthorized error, but this is
    // okay, since it means we reached the end of the method.
    assertThrows(Unauthorized.class, () -> {
      status = resultsHandler.handle(WebhookMocks.RESULT_EVENT);
    });
    // The status should be false since we are unauthorized
    Assertions.assertFalse(status);
  }
}
