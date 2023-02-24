package com.icetlab.performancebot.database.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHub;

/**
 * The GitHubRepository interface defines Spring Data MongoDB operations for working with GitHub
 * project data.
 */
public interface GitHubRepository extends MongoRepository<GitHub, String> {

  /**
   * Finds a GitHub project by its name and owner.
   *
   * @param name the name of the project to find.
   * @param owner the owner of the project to find.
   * @return the GitHub project with the specified name and owner.
   */
  @Query("{ 'name' : ?0, 'owner' : ?1 }")
  GitHub findByNameAndOwner(String name, String owner);

  /**
   * Finds a GitHub project by its id.
   *
   * @param id the id of the project to find.
   * @return an Optional containing the GitHub project with the specified id, or an empty Optional
   *         if not found.
   */
  @Query("{ 'id' : ?0}")
  Optional<GitHub> findById(String id);

  /**
   * Finds a GitHub project by its URL.
   *
   * @param url the URL of the project to find.
   * @return the GitHub project with the specified URL.
   */
  @Query("{ 'url' : ?0}")
  GitHub findByUrl(String url);

  /**
   * Finds all benchmark runs for a GitHub project by its id.
   *
   * @param id the id of the project to find runs for.
   * @return a List of all benchmark runs for the project with the specified id.
   */
  @Query("{ 'id' : ?0, 'runs' : { $exists : true } }")
  List<Benchmark> findAllRunsById(String id);

  /**
   * Returns the count of GitHub projects in the repository.
   *
   * @return the count of GitHub projects in the repository.
   */
  @Override
  long count();
}

