package com.icetlab.performancebot.stats;

import com.icetlab.performancebot.stats.generated.BenchmarkJMH;

/**
 * Interface for classes that creates issues from data of jmh benchmarks
 */
public interface BenchmarkIssueFormatter {

  public String formatBenchmarkIssue(BenchmarkJMH[] benchmarksJMH);
}
