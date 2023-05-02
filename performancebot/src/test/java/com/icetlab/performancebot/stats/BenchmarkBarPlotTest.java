package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;

import com.icetlab.performancebot.stats.visualization.BenchmarkBarPlot;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;


public class BenchmarkBarPlotTest {

  @Test
  public void test() {
    BenchmarkBarPlot benchmarkBarPlot = new BenchmarkBarPlot();
    List<Result> results = new ArrayList<>();
    results.add(new Result(Constants.bmResultSampleBenchmarkNewWay));
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String barPlot = benchmarkBarPlot.visualizeBenchmarkResults(method);
    System.out.println(barPlot);
    assertTrue(true);
  }
}
