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

  /**
   * Adds a benchmark run to the list of runs.
   * 
   * @param run
   */
  public void addRun(Benchmark run) {
    runs.add(run);
  }

  /**
   * Gets the id of the GitHub repository.
   * 
   * @return the id of the GitHub repository.
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the name of the GitHub repository.
   * 
   * @return the name of the GitHub repository.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the owner of the GitHub repository.
   * 
   * @return the owner of the GitHub repository.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Gets the url of the GitHub repository.
   * 
   * @return the url of the GitHub repository.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets the list of benchmark runs associated with the GitHub repository.
   * 
   * @return the list of benchmark runs associated with the GitHub repository.
   */
  public List<Benchmark> getRuns() {
    return runs;
  }

}
