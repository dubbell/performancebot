package com.icetlab.benchmarkworker.configuration;

import java.util.List;

public class BuildTask {

  private String path; // maven or gradle project path
  private List<String> tasks; // maven or gradle task

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
