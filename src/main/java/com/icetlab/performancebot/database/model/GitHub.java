package com.icetlab.performancebot.database.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A MongoDB model for a GitHub repository.
 */
@Document("github_projects")
public class GitHub {
  @Id
  private final String id;

  private final String name;
  private final String owner;
  private final String url;
  @DBRef
  private List<Benchmark> runs;

  public GitHub(String id, String name, String owner, String url, List<Benchmark> runs) {
    this.id = id;
    this.name = name;
    this.owner = owner;
    this.url = url;
    this.runs = runs;
  }

  public void addRun(Benchmark run) {
    runs.add(run);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return owner;
  }

  public String getUrl() {
    return url;
  }

  public List<Benchmark> getRuns() {
    return runs;
  }

}
