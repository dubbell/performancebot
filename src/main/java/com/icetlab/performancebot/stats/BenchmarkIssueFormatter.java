package com.icetlab.performancebot.stats;


/**
 * Interface for classes that creates issues from data of jmh benchmarks
 */
public interface BenchmarkIssueFormatter {

  String formatBenchmarkIssue(BenchmarkJMH[] benchmarksJMH);
}
