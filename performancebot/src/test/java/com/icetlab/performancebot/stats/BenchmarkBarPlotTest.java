package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;

import com.icetlab.performancebot.stats.visualization.BenchmarkBarPlot;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
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
    for (int i = 0; i < 20; i++) {
      results.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    }
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String mdBarPlot = benchmarkBarPlot.visualizeBenchmarkResults(method);
    try {
      Files.writeString(Path.of("test.md"), mdBarPlot);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertTrue(mdBarPlot.startsWith("## newWay"));
    Assertions.assertTrue(
        mdBarPlot.contains("![com.szatmary.peter.SampleBenchmarkTest.newWay](https://"));
  }

  /*
  Test that run configuration data for exactly ten run results are included, even if method has
  more than ten run results.
   */
  @Test
  public void testRunConfigurationsCorrectAmount() {
    BenchmarkBarPlot benchmarkBarPlot = new BenchmarkBarPlot();
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      results.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    }
    results.add(new Result(Constants.bmResultSampleBenchmarkNewWay));
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    String mdBarPlot = benchmarkBarPlot.visualizeBenchmarkResults(method);
    String dateRegex = "\\*\\s(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}\\s[0-9]{2}:[0-9]{2}";
    Pattern pattern = Pattern.compile(dateRegex);
    Matcher matcher = pattern.matcher(mdBarPlot);
    int matchCount = 0;
    while (matcher.find()) {
      matchCount++;
    }
    Assertions.assertEquals(10, matchCount);

  }

  /*
    Test that the ten most recent run results are included in the run configurations
   */
  @Test
  public void testGetNMostRecentRunResults() {
    BenchmarkBarPlot benchmarkBarPlot = new BenchmarkBarPlot();
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      results.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    }
    results.add(new Result(Constants.exampleResultNewWayRunConfigs)); // The most recent
    Method method = new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results);
    List<Result> tenMostRecent = BenchmarkBarPlot.getNMostRecentRunResults(method, 10);
    String measurementIterationsLastRunResult = FormatterUtils.getMeasurementIterations(
        tenMostRecent.get(tenMostRecent.size() - 1).getData());
    Assertions.assertEquals(10, tenMostRecent.size());
    Assertions.assertEquals("4", measurementIterationsLastRunResult);
  }
}
