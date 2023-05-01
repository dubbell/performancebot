package com.icetlab.performancebot.github.webhook;

import com.icetlab.performancebot.webhook.handlers.RepoHandler;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;

@SpringBootTest
public class RepoHandlerTest {
  @InjectMocks
  @Autowired
  private RepoHandler repoHandler;

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
  public void testHandleRepoDeletion() {
    String payload = """
        {
          "action": "removed",
          "installation": {
            "id": "an id"
          },
          "repositories_removed": [
            {"id": "a repo id"}
          ]

        }
        """;
    installationController.addInstallation("an id");
    installationController.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    status = repoHandler.handle(payload);
    assertTrue(status);
    status = repoHandler.handle(payload);
    assertFalse(status);
  }
}
