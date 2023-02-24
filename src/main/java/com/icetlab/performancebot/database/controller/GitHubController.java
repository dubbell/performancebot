package com.icetlab.performancebot.database.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHub;
import com.icetlab.performancebot.database.repository.GitHubRepository;

/**
 * The GitHubController class is a Spring controller for interacting with GitHub project and
 * benchmark data.
 */
@Repository
public class GitHubController {

  @Autowired
  private GitHubRepository gitHubProjectRepository;

  /**
   * Retrieves all GitHub projects.
   *
   * @return a List of all GitHub projects.
   */
  public List<GitHub> getProjects() {
    return gitHubProjectRepository.findAll();
  }

  /**
   * Retrieves a GitHub project by its id.
   *
   * @param id the id of the project to retrieve.
   * @return a GitHub object representing the project, or null if not found.
   */
  public GitHub getProjectById(String id) {
    Optional<GitHub> project = gitHubProjectRepository.findById(id);
    return project.orElse(null);
  }

  /**
   * Retrieves all benchmark runs for a GitHub project.
   *
   * @param id the id of the project to retrieve runs for.
   * @return a List of all benchmark runs for the project.
   */
  public List<Benchmark> getRunsById(String id) {
    return gitHubProjectRepository.findAllRunsById(id);
  }

  /**
   * Adds a new GitHub project.
   *
   * @param id the id of the project to add.
   * @param name the name of the project to add.
   * @param owner the owner of the project to add.
   * @param url the URL of the project to add.
   */
  public void addProject(String id, String name, String owner, String url) {
    Optional<GitHub> project = gitHubProjectRepository.findById(id);
    if (project.isPresent()) {
      return;
    }
    gitHubProjectRepository.insert(new GitHub(id, name, owner, url, new ArrayList<>()));
  }

  /**
   * Adds a new benchmark run to a GitHub project.
   *
   * @param projectId the id of the project to add the run to.
   * @param run the benchmark run data to add.
   */
  public void addRun(String projectId, String run) {
    Optional<GitHub> project = gitHubProjectRepository.findById(projectId);
    if (project.isEmpty()) {
      return;
    }
    List<Benchmark> runs = project.get().getRuns();
    Benchmark benchmark = new Benchmark(runs.size() + "", run, projectId);
    runs.add(benchmark);
    gitHubProjectRepository.save(project.get());
  }
}
