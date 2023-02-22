package com.icetlab.benchmark_worker;


import com.icetlab.performancebot.PerformanceBot;
import org.springframework.boot.SpringApplication;


/**
 * Spring boot application to be run in containers.

 * Is given tasks to complete by the performancebot, which consist of:
 * 1. Cloning the given repository into a local directory.
 * 2. Compiling and running all specified benchmarks in the cloned repository.
 * 3. Sending the results to the database.
 * 4. Performing statistical analysis.
 * 5. Sending the result of the analysis to the remote repository as a Github Issue.
 */
public class BenchmarkWorker {

  public static void main(String[] args) {
    SpringApplication.run(PerformanceBot.class, args);
  }
}