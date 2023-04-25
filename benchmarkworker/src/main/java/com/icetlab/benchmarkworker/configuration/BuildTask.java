package com.icetlab.benchmarkworker.configuration;

import java.util.List;

/**
 * Representation of a gradle or maven project with a path (from the root of the repository), and at least
 * one task (tasks represent gradle tasks or maven goals).
 */
public class BuildTask {

  private String path; // maven or gradle project path
  private List<String> tasks; // maven goals or gradle tasks

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<String> getTasks() {
    return tasks;
  }

  public void setTasks(List<String> tasks) {
    this.tasks = tasks;
  }
}
