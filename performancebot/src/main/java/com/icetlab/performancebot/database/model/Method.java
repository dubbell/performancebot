package com.icetlab.performancebot.database.model;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a method entry in the database. It contains all the benchmark runs for the method. Use
 * this to read data regarding a specific benchmark.
 */
@Document(collection = "methods")
public class Method {

  private final String methodName;
  private final List<Result> runResults;

  /**
   * Creates a new method entry in the database.
   * 
   * @param methodName
   * @param runResults
   */
  public Method(String methodName, List<Result> runResults) {
    this.methodName = methodName;
    this.runResults = runResults;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<Result> getRunResults() {
    return runResults;
  }

  public void addResult(Result result) {
    this.runResults.add(result);
  }
}
