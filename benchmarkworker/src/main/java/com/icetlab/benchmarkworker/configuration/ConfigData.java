package com.icetlab.benchmarkworker.configuration;

import java.util.List;

/**
 * Class representing the contents of a .yaml configuration file.
 */
public class ConfigData {

  private String buildTool; // maven or gradle
  private String options; // jmh options
  private List<BuildTask> buildTasks; // tasks executed to build project, each containing a project path and task name
  private String jmhJar; // path to jar once project has been built using the buildTasks


  public String getBuildTool() {
    return buildTool;
  }

  public void setBuildTool(String buildTool) {
    this.buildTool = buildTool;
  }

  public List<BuildTask> getBuildTasks() {
    return buildTasks;
  }

  public void setBuildTasks(List<BuildTask> buildTasks) {
    this.buildTasks = buildTasks;
  }

  public String getOptions() {
    return options;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  public String getJmhJar() {
    return jmhJar;
  }

  public void setJmhJar(String jmhJar) {
    this.jmhJar = jmhJar;
  }
}
