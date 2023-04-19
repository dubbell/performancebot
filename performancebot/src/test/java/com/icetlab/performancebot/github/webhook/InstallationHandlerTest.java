package com.icetlab.performancebot.github.webhook;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.service.InstallationService;

@SpringBootTest
public class InstallationHandlerTest {
  @InjectMocks
  @Autowired
  private InstallationHandler installationHandler;

  @InjectMocks
  @Autowired
  private InstallationService installationController;

  @Autowired
  MongoTemplate mongoTemplate;

  @AfterEach
  public void tearDown() {
    mongoTemplate.dropCollection(Installation.class);
  }

  @Test
  public void testHandleNewInstall() {
    installationHandler.handle(WebhookMocks.INSTALL_EVENT);
    Installation inst = installationController.getInstallationById("123456");
    assertNotNull(inst);
  }

  @Test
  public void testHandleUninstall() {
    installationHandler.handle(WebhookMocks.UNINSTALL_EVEMT);
    assertThrows(NoSuchElementException.class, () -> {
      installationController.getInstallationById("123456");
    });
  }
}
