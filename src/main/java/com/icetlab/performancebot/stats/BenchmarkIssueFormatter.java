package com.icetlab.performancebot.stats;


/**
 * Interface for classes that creates issues from data of jmh benchmarks
 */
public interface BenchmarkIssueFormatter {

  public String formatBenchmarkIssue(BenchmarkJMH benchmarkJMH);
}
