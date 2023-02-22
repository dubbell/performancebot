package com.icetlab.performancebot.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("benchmarks")
public class Benchmark {
  @Id
  private final String id;

  // FIXME: This could be a GraphQL object
  private final String runData;

  private final String projectId;

  public Benchmark(String id, String runData, String projectId) {
    this.id = id;
    this.runData = runData;
    this.projectId = projectId;
  }

  public String getId() {
    return id;
  }

  public String getRunData() {
    return runData;
  }

  public String getProjectId() {
    return projectId;
  }
}
