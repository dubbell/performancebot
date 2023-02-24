package com.icetlab.performancebot.database.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHub;
import com.icetlab.performancebot.database.repository.GitHubRepository;

@Component
public class GitHubController {


  @Autowired
  private GitHubRepository gitHubProjectRepository;

  public List<GitHub> getProjects() {
    return gitHubProjectRepository.findAll();
  }

  public GitHub getProjectById(String id) {
    Optional<GitHub> project = gitHubProjectRepository.findById(id);
    return project.orElse(null);
  }

  public List<Benchmark> getRunsById(String id) {
    return gitHubProjectRepository.findAllRunsById(id);
  }

  public void addProject(String id, String name, String owner, String url) {
    Optional<GitHub> project = gitHubProjectRepository.findById(id);
    if (project.isPresent()) {
      return;
    }
    gitHubProjectRepository.insert(new GitHub(id, name, owner, url, new ArrayList<>()));
  }

  /**
   * Adds a run to a project
   * 
   * @param projectId the project id
   * @param run the json for a run
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
