package com.icetlab.performancebot.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A MongoDB model for a benchmark run.
 */
@Document("benchmarks")
public class Benchmark {
  @Id
  private final String id;

  private final String runData;

  private final String projectId;

  public Benchmark(String id, String runData, String projectId) {
    this.id = id;
    this.runData = runData;
    this.projectId = projectId;
  }

  /**
   * Gets the id of the benchmark run.
   * 
   * @return the id of the benchmark run.
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the JSON data associated with the benchmark run.
   * 
   * @return the JSON data associated with the benchmark run.
   */
  public String getRunData() {
    return runData;
  }

  /**
   * Gets the id of the project associated with the benchmark run.
   * 
   * @return the id of the project associated with the benchmark run.
   */
  public String getProjectId() {
    return projectId;
  }
}
