package com.icetlab.performancebot.stats;


/**
 * A very simple issue that shows the score of a benchmark
 */
public class SimpleIssue implements IssueFormatter {

  @Override
  public String formatBenchmarkIssue(BenchmarkJMH benchmarkJMH) {
    String benchmark = benchmarkJMH.getBenchmark().toString();
    String score = benchmarkJMH.getPrimaryMetric().getScore().toString();
    String issue = "Score of " + benchmark + ": " + score;
    return issue;
  }
}
