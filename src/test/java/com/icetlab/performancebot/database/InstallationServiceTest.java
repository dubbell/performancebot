package com.icetlab.performancebot.database;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.service.InstallationService;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    installationService.addInstallation("123");
    Mockito.when(installationRepository.findById("123")).thenReturn(Optional.of(new Installation("123", new ArrayList<>())));
    Installation installation = installationService.getInstallationById("123");
    assertEquals("123", installation.getInstallationId());
  }

  @Test
  public void testGetInstallationByIdException() {
    assertThrows(NoSuchElementException.class, () -> {
      installationService.getInstallationById("123");
    });
  }

  @Test
  public void testAddInstallationException(){
    installationService.addInstallation("12345");
    Mockito.when(installationRepository.findById("12345")).thenReturn(Optional.of(new Installation("123", new ArrayList<>())));
    try {
      installationService.addInstallation("12345");
    } catch (RuntimeException e) {
      assertEquals("The id already exists", e.getMessage());
    }
  }

}
