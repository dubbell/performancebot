package com.icetlab.performancebot.stats;

import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.springframework.stereotype.Component;

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
      url = ImageKitUploader.uploadImage(writeChartToPng(methodBarPlot, method.getMethodName()),
          encodedBarPlot);
    } catch (Exception e) {
      url =
          "https://ih1.redbubble.net/image.1539738010.3563/flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg";
    }
    sb.append(String.format("![%s](%s)\n", method.getMethodName(), url));
    sb.append("\nTODO: implement additional info\n");
    return sb.toString();
  }

  /**
   * @param method the method which results should be visualized as bar plot png
   * @return path of the bar plot png
   * @throws IOException if png image cannot be created
   */
  private CategoryChart buildBarplot(Method method) throws IOException {
    // FIXME: Loops through all results, it should be the 10 latest
    CategoryChart barPlot = new CategoryChartBuilder().width(600).height(600)
        .title(method.getMethodName()).xAxisTitle("Date").yAxisTitle("Score").build();
    List<Result> results = method.getRunResults();
    List<String> timestamps = results.stream()
        .map((x) -> new SimpleDateFormat("dd/MM/yyyy HH:MM").format(x.getAddedAt())).toList();
    List<Double> scores = results.stream()
        .map((x) -> Double.parseDouble(FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
        .toList();
    String header = FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName());
    barPlot.addSeries(header, timestamps, scores);
    barPlot.getStyler().setLegendVisible(false);
    return barPlot;
  }

  /**
   * Takes a Category chart and encodes it as a base64 string
   *
   * @param categoryChart the chart to be encoded
   * @return a base64 string
   * @throws IOException
   */
  private String encodeChartToBase64(CategoryChart categoryChart) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BitmapEncoder.saveBitmap(categoryChart, outputStream, BitmapEncoder.BitmapFormat.PNG);
    byte[] imageBytes = outputStream.toByteArray();
    return Base64.getEncoder().encodeToString(imageBytes);
  }

  private String writeChartToPng(CategoryChart chart, String fileName) throws IOException {
    BitmapEncoder.saveBitmapWithDPI(chart, fileName, BitmapFormat.PNG, 300);
    return fileName + ".png";
  }

}
