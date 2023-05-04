package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;

import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.model.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.icetlab.performancebot.stats.visualization.BenchmarkTable;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class BenchmarkTableTest {


  /**
   * Test one row table
   */
  @Test
  public void testVisualizeBenchmarkResults() {
    BenchmarkTable benchmarkTable = new BenchmarkTable();
    List<Result> results = new ArrayList<>();
    results.add(new Result(Constants.bmResultSampleBenchmarkNewWay));
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String table = benchmarkTable.visualizeBenchmarkResults(method);
    assertTrue(table.startsWith("""
        ## newWay
                
        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|"""));
    assertTrue(table.endsWith("33.85525 | 20.2825505 | 27.06890025 | ms/op |\n\n"));
  }

  /**
   * Test multiple rows in a table as well as order
   */
  @Test
  public void testVisualizeMultipleResults() {
    BenchmarkTable benchmarkTable = new BenchmarkTable();
    List<Result> results = new ArrayList<>();
    results.add(new Result(Constants.bmResultSampleBenchmarkNewWay));
    results.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    results.add(new Result(Constants.exampleResultSampleBenchmarknewWay2));
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String table = benchmarkTable.visualizeBenchmarkResults(method);
    List<String> mdRows = Arrays.asList(table.split("\n")).subList(4,7);

    assertTrue(table.startsWith("""
        ## newWay
                
        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|"""));
    assertTrue(mdRows.get(0).contains("| 33.85525 | 20.2825505 | 27.06890025 | ms/op |"));
    assertTrue(mdRows.get(1).contains("| 22.92065025 | 13.79857475 | 20.3596125 | ms/op |"));
    assertTrue(mdRows.get(2).contains("| 24.1234554 | 14.2345123 | 17.3596125 | ms/op |"));

  }

}
