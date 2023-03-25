package com.icetlab.performancebot.database;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.service.InstallationService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.util.ReflectionTestUtils;

public class InstallationServiceTest {
  @InjectMocks
  private InstallationService installationService;
  @Mock
  private InstallationRepository installationRepository;

  @BeforeEach
  public void setup() {
    installationRepository = Mockito.mock(InstallationRepository.class);
    installationService = spy(new InstallationService());
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

  @Test
  public void testAddInstallation() {
    String installationId = "123456";
    Mockito.when(installationRepository.findById(installationId))
        .thenReturn(Optional.of(new Installation(installationId, new ArrayList<>())));
    installationService.addInstallation(installationId);
    Installation installation = installationService.getInstallationById(installationId);
    assertEquals(installationId, installation.getInstallationId());
  }

  @Test
  public void testAddRepo() {
    // Setup mocks
    String installationId = "123456";
    GitHubRepo repo = new GitHubRepo(installationId, new HashSet<>(), "123");
    MongoTemplate mongoTemplateMock = Mockito.mock(MongoTemplate.class);
    ReflectionTestUtils.setField(installationService, "mongoTemplate", mongoTemplateMock);

    // Add installation
    Mockito.when(installationService.installationExists(installationId)).thenReturn(false);
    Mockito.when(installationRepository.findById(installationId))
        .thenReturn(Optional.of(new Installation(installationId, new ArrayList<>())));
    installationService.addInstallation(installationId);

    // Add repo to installation
    Mockito.when(installationService.installationExists(installationId)).thenReturn(true);
    installationService.addRepoToInstallation(installationId, repo);

    // Verify
    Mockito.verify(mongoTemplateMock).updateFirst(
        Query.query(Criteria.where("_id").is(installationId)), new Update().push("repos", repo),
        Installation.class);
  }

  @Test
  public void testAddRepoFail() {
    // Setup mocks
    String installationId = "123456";
    GitHubRepo repo = new GitHubRepo(installationId, new HashSet<>(), "123");
    MongoTemplate mongoTemplateMock = Mockito.mock(MongoTemplate.class);
    ReflectionTestUtils.setField(installationService, "mongoTemplate", mongoTemplateMock);

    // Add repo to installation that does not exist
    Mockito.when(installationService.installationExists(installationId)).thenReturn(false);
    try {
      installationService.addRepoToInstallation(installationId, repo);
    } catch (NoSuchElementException e) {
      assertTrue(true);
    }

    // Verify
    Mockito.verify(mongoTemplateMock, times(0)).updateFirst(
        Query.query(Criteria.where("_id").is(installationId)), new Update().push("repos", repo),
        Installation.class);
  }

}
