package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;

import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.model.Method;
import java.util.ArrayList;
import java.util.List;

import com.icetlab.performancebot.stats.visualization.BenchmarkTable;
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

}
