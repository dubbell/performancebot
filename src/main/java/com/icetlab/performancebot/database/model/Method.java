package com.icetlab.performancebot.database.model;

import java.util.List;

/**
 * Represents a method entry in the database. It contains all the benchmark runs for the method. Use
 * this to read data regarding a specific benchmark.
 */
public class Method {

  private final String methodName;
  private final List<String> runResults;

  public Method(String methodName, List<String> runResults) {
    this.methodName = methodName;
    this.runResults = runResults;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<String> getRunResults() {
    return runResults;
  }
}
