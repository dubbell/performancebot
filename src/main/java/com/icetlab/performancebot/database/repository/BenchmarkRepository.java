package com.icetlab.performancebot.database.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.icetlab.performancebot.database.model.Benchmark;

/**
 * The BenchmarkRepository interface is a Spring Data MongoDB repository for interacting with
 * benchmark data.
 */
@Repository
public interface BenchmarkRepository extends MongoRepository<Benchmark, String> {

  /**
   * Finds a benchmark by its id.
   *
   * @param id the id of the benchmark to find.
   * @return an Optional<Benchmark> containing the benchmark if found, or an empty Optional if not
   *         found.
   */
  @Query("{ 'id' : ?0 }")
  Optional<Benchmark> findById(String id);
}
