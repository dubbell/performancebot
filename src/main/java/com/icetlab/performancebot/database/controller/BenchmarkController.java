package com.icetlab.performancebot.database.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.repository.BenchmarkRepository;

/**
 * The BenchmarkController class is used to interact with the database.
 */
@Repository
public class BenchmarkController {

  /**
   * The benchmarkRepository variable is an instance of BenchmarkRepository that is used to interact
   * with the database.
   */
  @Autowired
  private BenchmarkRepository benchmarkRepository;

  /**
   * Returns the benchmark data associated with the specified id.
   * 
   * @param id the id of the benchmark data to retrieve.
   * @return the benchmark data associated with the specified id, or null if it does not exist.
   */
  public Benchmark getBenchmarkById(String id) {
    return benchmarkRepository.findById(id).orElse(null);
  }

  /**
   * Adds a new benchmark data entry to the database.
   * 
   * @param runData the JSON data associated with the benchmark run.
   * @param projectId the id of the project associated with the benchmark run.
   */
  public void addBenchmark(String runData, String projectId) {
    benchmarkRepository.insert(new Benchmark(benchmarkRepository.count() + "", runData, projectId));
  }
}
