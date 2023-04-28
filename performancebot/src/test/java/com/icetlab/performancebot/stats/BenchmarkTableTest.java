package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;

import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.model.Method;
import java.util.ArrayList;
import java.util.List;

import com.icetlab.performancebot.stats.visualization.BenchmarkTable;
import org.junit.jupiter.api.Test;

public class BenchmarkTableTest {

  @Test
  public void test() {
    BenchmarkTable benchmarkTable = new BenchmarkTable();
    List<Result> results = new ArrayList<>();
    results.add(new Result(Constants.res_newWay));
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String table = benchmarkTable.visualizeBenchmarkResults(method);
    System.out.println(table);
    assertTrue(true);
  }

}
