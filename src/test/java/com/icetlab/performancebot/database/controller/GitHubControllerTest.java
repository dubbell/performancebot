package com.icetlab.performancebot.database.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHub;
import com.icetlab.performancebot.database.repository.GitHubRepository;

@SpringBootTest
public class GitHubControllerTest {

  @Mock
  private GitHubRepository gitHubProjectRepository;

  @InjectMocks
  private GitHubController gitHubController;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    gitHubController = new GitHubController();
    ReflectionTestUtils.setField(gitHubController, "gitHubProjectRepository",
        gitHubProjectRepository);
  }

  @Test
  public void testGetProjects() {
    // Arrange
    List<GitHub> projects = new ArrayList<>();
    projects.add(new GitHub("1", "Project 1", "Owner 1", "http://project1.com", new ArrayList<>()));
    projects.add(new GitHub("2", "Project 2", "Owner 2", "http://project2.com", new ArrayList<>()));
    when(gitHubProjectRepository.findAll()).thenReturn(projects);

    // Act
    List<GitHub> result = gitHubController.getProjects();

    // Assert
    assertEquals(2, result.size());
    assertEquals("1", result.get(0).getId());
    assertEquals("Project 1", result.get(0).getName());
    assertEquals("Owner 1", result.get(0).getOwner());
    assertEquals("http://project1.com", result.get(0).getUrl());
    assertEquals("2", result.get(1).getId());
    assertEquals("Project 2", result.get(1).getName());
    assertEquals("Owner 2", result.get(1).getOwner());
    assertEquals("http://project2.com", result.get(1).getUrl());
  }

  @Test
  public void testGetProjectById() {
    // Arrange
    GitHub project =
        new GitHub("1", "Project 1", "Owner 1", "http://project1.com", new ArrayList<>());
    when(gitHubProjectRepository.findById("1")).thenReturn(Optional.of(project));

    // Act
    GitHub result = gitHubController.getProjectById("1");

    // Assert
    assertEquals("1", result.getId());
    assertEquals("Project 1", result.getName());
    assertEquals("Owner 1", result.getOwner());
    assertEquals("http://project1.com", result.getUrl());
  }

  @Test
  public void testGetRunsById() {
    // Arrange
    List<Benchmark> runs = new ArrayList<>();
    runs.add(new Benchmark("1", "Run 1", "1"));
    runs.add(new Benchmark("2", "Run 2", "1"));
    when(gitHubProjectRepository.findAllRunsById("1")).thenReturn(runs);

    // Act
    List<Benchmark> result = gitHubController.getRunsById("1");

    // Assert
    assertEquals(2, result.size());
    assertEquals("1", result.get(0).getId());
    assertEquals("Run 1", result.get(0).getRunData());
    assertEquals("1", result.get(0).getProjectId());
    assertEquals("2", result.get(1).getId());
    assertEquals("Run 2", result.get(1).getRunData());
    assertEquals("1", result.get(1).getProjectId());
  }

  @Test
  public void testAddProject() {
    // Arrange
    GitHub project =
        new GitHub("1", "Project 1", "Owner 1", "http://project1.com", new ArrayList<>());
    when(gitHubController.addProject(project)).thenReturn(project);

    // Act
    GitHub result = gitHubController.addProject(project);

    // Assert
    assertEquals("1", result.getId());
    assertEquals("Project 1", result.getName());
    assertEquals("Owner 1", result.getOwner());
    assertEquals("http://project1.com", result.getUrl());
  }

  @Test
  public void testAddRun() {
    // Arrange
    GitHub project = Mockito.mock(GitHub.class);
    List<Benchmark> runs = new ArrayList<>();
    Mockito.when(project.getRuns()).thenReturn(runs);
    Mockito.when(gitHubProjectRepository.findById("project-id")).thenReturn(Optional.of(project));
    Benchmark benchmark = new Benchmark("0", "run-data", "project-id");

    // Act
    Benchmark result = gitHubController.addRun("project-id", "run-data");

    // Assert
    Mockito.verify(gitHubProjectRepository).save(project);
    assertEquals(1, runs.size());
    assertEquals(benchmark.getId(), result.getId());
    assertEquals(benchmark.getRunData(), result.getRunData());
    assertEquals(benchmark.getProjectId(), result.getProjectId());

  }

  @Test
  public void testAddRunWithNonexistentProject() {
    // Arrange
    Mockito.when(gitHubProjectRepository.findById("nonexistent-project-id"))
        .thenReturn(Optional.empty());

    // Act
    Benchmark result = gitHubController.addRun("nonexistent-project-id", "run-data");

    // Assert
    Mockito.verify(gitHubProjectRepository, Mockito.never()).save(Mockito.any());
    assertNull(result);
  }
}
