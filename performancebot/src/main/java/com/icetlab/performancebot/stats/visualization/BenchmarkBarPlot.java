package com.icetlab.performancebot.stats.visualization;

import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.icetlab.performancebot.stats.FormatterUtils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;

public class BenchmarkBarPlot implements VisualizationStrategy {

  @Override
  public String visualizeBenchmarkResults(Method method) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("## %s\n",
        FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName())));
    String url;
    try {
      CategoryChart methodBarPlot = buildBarplot(method);
      String encodedBarPlot = encodeChartToBase64(methodBarPlot);
      url = ImageKitUploader.uploadImage(method.getMethodName() + ".png",
          encodedBarPlot);
    } catch (Exception e) {
      url =
          "https://ih1.redbubble.net/image.1539738010.3563/flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg";
    }
    sb.append(String.format("![%s](%s)\n", method.getMethodName(), url));
    sb.append("\n").append(formatRunConfigurations(method)).append("\n");
    return sb.toString();
  }

  /**
   * @param method the method which results should be visualized as bar plot
   * @return the CategoryChart visualizing methods run results
   */
  private CategoryChart buildBarplot(Method method) {
    CategoryChart barPlot = new CategoryChartBuilder().width(600).height(600)
        .title(method.getMethodName()).xAxisTitle("Date").yAxisTitle("Score").build();
    List<Result> results = getNMostRecentRunResults(method, 10);
    List<String> timestamps = results.stream()
        .map((x) -> new SimpleDateFormat("dd/MM/yyyy HH:mm").format(x.getAddedAt())).toList();
    List<Double> scores = results.stream()
        .map((x) -> Double.parseDouble(FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
        .toList();
    String header = FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName());
    barPlot.addSeries(header, timestamps, scores);
    barPlot.getStyler().setLegendVisible(false);
    barPlot.getStyler().setXAxisLabelRotation(45);
    return barPlot;
  }

  /**
   * Takes a Category chart and encodes it as a base64 string
   *
   * @param categoryChart the chart to be encoded
   * @return a base64 string
   * @throws IOException if categoryChart cannot be encoded as base64
   */
  private String encodeChartToBase64(CategoryChart categoryChart) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BitmapEncoder.saveBitmap(categoryChart, outputStream, BitmapEncoder.BitmapFormat.PNG);
    byte[] imageBytes = outputStream.toByteArray();
    return Base64.getEncoder().encodeToString(imageBytes);
  }

  private String formatRunConfigurations(Method method) {
    StringBuilder sb = new StringBuilder();
    sb.append("""
        <details>
          <summary>Run configurations</summary>
          
        """);
    List<Result> runResults = getNMostRecentRunResults(method, 10);
    for (Result runResult : runResults) {
      String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(runResult.getAddedAt());
      String mode = FormatterUtils.getMode(runResult.getData());
      String measurementIterations = FormatterUtils.getMeasurementIterations(runResult.getData());
      String unit = FormatterUtils.getUnitFromPrimaryMetric(runResult.getData());
      sb.append("* ").append(timestamp).append("\n\t");
      sb.append("* Mode: ").append(mode).append("\n\t");
      sb.append("* Score unit: ").append(unit).append("\n\t");
      sb.append("* Measurement Iterations: ").append(measurementIterations).append("\n\n");
    }
    sb.append("</details>");
    return sb.toString();
  }

  public static List<Result> getNMostRecentRunResults(Method method, int n) {
    List<Result> allResults = method.getRunResults();
    return allResults.subList(Math.max(allResults.size() - n, 0), allResults.size());
  }

}
