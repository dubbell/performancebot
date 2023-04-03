package com.icetlab.benchmarkworker.configuration;

import java.util.List;

/**
 * Class representing the contents of a .yaml configuration file.
 */
public class ConfigData {
  private String language;
  private String buildTool;
  private String options;
  private List<String> gradleBuildTasks;
  private String jmhJar;

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getBuildTool() {
    return buildTool;
  }

  public void setBuildTool(String buildTool) {
    this.buildTool = buildTool;
  }

  public List<String> getGradleBuildTasks() {
    return gradleBuildTasks;
  }

  public void setGradleBuildTasks(List<String> gradleBuildTasks) {
    this.gradleBuildTasks = gradleBuildTasks;
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
