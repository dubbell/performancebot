package com.icetlab.performancebot.benchmark;

class JMHBenchmark implements IBenchmark {

  public JMHBenchmark() {

  }
  @Override
  public void benchmark() {
    System.out.println("Benchmark!");
  }
}