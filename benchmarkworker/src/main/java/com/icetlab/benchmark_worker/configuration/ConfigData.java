package com.icetlab.benchmarkworker.configuration;

import java.util.List;

/**
 * Class representing the contents of a .yaml configuration file.
 */
public class ConfigData {
  private String language;
  private String buildTool;
  private List<String> include;
  private List<String> exclude;
  private int forks;
  private List<String> gradleBuildTasks;
  private String gradleClassPath;

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

  public List<String> getInclude() {
    return include;
  }

  public void setInclude(List<String> include) {
    this.include = include;
  }

  public List<String> getExclude() {
    return exclude;
  }

  public void setExclude(List<String> exclude) {
    this.exclude = exclude;
  }

  public int getForks() {
    return forks;
  }

  public void setForks(int forks) {
    this.forks = forks;
  }

  public List<String> getGradleBuildTasks() {
    return gradleBuildTasks;
  }

  public void setGradleBuildTasks(List<String> gradleBuildTasks) {
    this.gradleBuildTasks = gradleBuildTasks;
  }

  public String getGradleClassPath() {
    return gradleClassPath;
  }

  public void setGradleClassPath(String gradleClassPath) {
    this.gradleClassPath = gradleClassPath;
  }
}
