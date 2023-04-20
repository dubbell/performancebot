package com.icetlab.performancebot.database.model;

import java.util.Set;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a GitHub repository in an installation. It contains all the methods that are
 * benchmarked.
 */
@Document(collection = "repos")
public class GitHubRepo {

  private final String name;
  private final String repoId;
  private final Set<Method> methods;

  public GitHubRepo(String repoId, Set<Method> methods, String name) {
    this.repoId = repoId;
    this.methods = methods;
    this.name = name;
  }

  public String getRepoId() {
    return repoId;
  }

  public Set<Method> getMethods() {
    return methods;
  }

  public String getName() {
    return name;
  }
}
