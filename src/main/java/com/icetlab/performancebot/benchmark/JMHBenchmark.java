package com.icetlab.performancebot.benchmark;

/**
 * Runs JMH benchmarks.
 */
public class JMHBenchmark implements IBenchmark {

  public JMHBenchmark() {

  }

  @Override
  public void benchmark() {
    System.out.println("Benchmark!");
  }
}