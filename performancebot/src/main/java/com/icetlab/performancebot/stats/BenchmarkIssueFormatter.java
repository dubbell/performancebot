package com.icetlab.performancebot.stats;

/**
 * Interface for classes that creates issues from data of jmh benchmarks
 */
public interface BenchmarkIssueFormatter {

  /**
   * Returns a markdown formatted string which is used the body in an issue.
   * 
   * It parses json strings, therefore we need json formatted jmhResults.
   * 
   * @param jmhResults json formatted jmh results
   * @return markdown formatted string
   */
  String formatBenchmarkIssue(String jmhResults);
}
