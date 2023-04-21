package com.icetlab.performancebot.github.webhook;

import static org.junit.Assert.assertThrows;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.service.InstallationService;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

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
    boolean handled = installationHandler.handle(WebhookMocks.INSTALL_EVENT);
    Installation inst = installationController.getInstallationById("123456");
    Assertions.assertNotNull(inst);
    Assertions.assertTrue(handled);
  }

  @Test
  public void testHandleUninstall() {
    boolean handled = installationHandler.handle(WebhookMocks.UNINSTALL_EVENT);
    assertThrows(NoSuchElementException.class, () -> {
      installationController.getInstallationById("123456");
    });
    Assertions.assertTrue(handled);
  }
}
