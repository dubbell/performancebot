package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;

import com.icetlab.performancebot.stats.visualization.BenchmarkBarPlot;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;


public class BenchmarkBarPlotTest {

  @Test
  public void testVisualizeBenchmarkResults() {
    BenchmarkBarPlot benchmarkBarPlot = new BenchmarkBarPlot();
    List<Result> results = new ArrayList<>();
    results.add(new Result(Constants.bmResultSampleBenchmarkNewWay));
    results.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String mdBarPlot = benchmarkBarPlot.visualizeBenchmarkResults(method);
    assertTrue(mdBarPlot.startsWith("## newWay"));
    assertTrue(mdBarPlot.contains("![com.szatmary.peter.SampleBenchmarkTest.newWay](https://"));
  }
}
