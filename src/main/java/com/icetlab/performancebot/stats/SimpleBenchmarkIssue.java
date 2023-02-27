package com.icetlab.performancebot.stats;


/**
 * A very simple issue that shows the score of a benchmark
 */
public class SimpleBenchmarkIssue implements BenchmarkIssueFormatter {

  @Override
  public String formatBenchmarkIssue(BenchmarkJMH[] benchmarksJMH) {
    BenchmarkJMH benchmarkJMH = benchmarksJMH[0];                         // Take the first benchmark
    String benchmark = benchmarkJMH.getBenchmark().toString();
    String score = benchmarkJMH.getPrimaryMetric().getScore().toString();
    String issue = "Score of " + benchmark + ": " + score;
    return issue;
  }
}
