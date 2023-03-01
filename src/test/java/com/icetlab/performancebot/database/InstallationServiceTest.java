package com.icetlab.performancebot.database;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.service.InstallationService;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class InstallationServiceTest {
  @InjectMocks
  private InstallationService installationService;
  @Mock
  private InstallationRepository installationRepository;

  @BeforeEach
  public void setup() {
    installationRepository = Mockito.mock(InstallationRepository.class);
    installationService = new InstallationService();
    ReflectionTestUtils.setField(installationService, "repo", installationRepository);
  }

  @Test
  public void testGetInstallationById() {
    String installationId = "123";
    Mockito.when(installationRepository.findById(installationId))
        .thenReturn(Optional.of(new Installation(installationId, new ArrayList<>())));
    installationService.addInstallation(installationId);
    Installation installation = installationService.getInstallationById("123");
    assertEquals("123", installation.getInstallationId());
  }

  @Test
  public void testGetInstallationByIdException() {
    String installationId = "1234";
    assertThrows(NoSuchElementException.class, () -> {
      installationService.getInstallationById(installationId);
    });
  }

  @Test
  public void testAddInstallationException() {
    String installationId = "12345";
    Mockito.when(installationRepository.findById(installationId))
        .thenReturn(Optional.of(new Installation(installationId, new ArrayList<>())));
    installationService.addInstallation(installationId);
    try {
      installationService.addInstallation(installationId);
    } catch (RuntimeException e) {
      assertEquals("The id already exists", e.getMessage());
    }
  }

}
