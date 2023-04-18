package com.icetlab.performancebot.stats;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.service.InstallationService;

@Component
public class BarPlotIssueFormatter implements BenchmarkIssueFormatter {
  @Autowired
  InstallationService installationService;

  /**
   * Returns a markdown formatted issue with bar plots which come in images.
   */
  @Override
  public String formatBenchmarkIssue(String jmhResults) {
    JsonNode node;
    try {
      node = new ObjectMapper().readTree(jmhResults);
      String installationId = node.get("installation_id").asText();
      String repoId = node.get("repo_id").asText();
      List<String> methodNames = FormatterUtils.getMethodNamesFromCurrentRun(jmhResults);
      Set<Method> methods = FormatterUtils.filterMethodsFromCurrentRun(
          installationService.getMethodsFromRepo(installationId, repoId), methodNames);
      Map<String, List<Method>> classes = FormatterUtils.groupMethodsByClassName(methods);
      String issueBody = formatIssueBody(classes);
      return issueBody;
    } catch (JsonProcessingException e) {
      return e.toString();
    }
  }

  /**
   * <p>
   * Builds an issue based on the structure of the classes map
   * </p>
   * 
   * <pre>
   * <code>
   * for class in classes:
   *   # ClassName
   *   for method in methods:
   *     ## MethodName
   *     ![methodName](url)
   *     other run information
   * </code>
   * </pre>
   *
   * @param classes the classes and their methods to be formatted
   * @return the classes
   */
  private String formatIssueBody(Map<String, List<Method>> classes) {
    StringBuilder sb = new StringBuilder();
    for (String className : classes.keySet()) {
      sb.append(String.format("# %s\n", className));
      for (Method method : classes.get(className)) {
        sb.append(formatMethodBody(method));
      }
    }
    return sb.toString();
  }

  /**
   * Takes a method and returns a markdown String containing method header, run results plot and
   * configuration results
   * 
   * @return
   */
  private String formatMethodBody(Method method) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("## %s\n", method.getMethodName()));
    String path;
    try {
      path = createImageFromResults(method.getRunResults(), method.getMethodName());
    } catch (IOException e) {
      path =
          "https://ih1.redbubble.net/image.1539738010.3563/flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg";
    }
    sb.append(String.format("![%s](%s)\n", method.getMethodName(), uploadImageAndGetUrl(path)));
    sb.append("TODO: implement additional info\n");
    return sb.toString();
  }

  private String uploadImageAndGetUrl(String path) {
    // TODO: upload to imagekit and get url
    return path;
  }

  /*
   * private void createCategoryChartFromMap(String className, List<Method> methods) { List<Result>
   * results = new ArrayList<>(); for (Method method : methods) {
   * results.addAll(method.getRunResults()); createImageFromResults(results,
   * method.getMethodName()); results.clear(); } }
   */

  private String createImageFromResults(List<Result> results, String header) throws IOException {
    CategoryChart chart = new CategoryChartBuilder().width(600).height(600).title(header)
        .xAxisTitle("Date").yAxisTitle("Score").build();
    List<String> timestamps = results.stream()
        .map((x) -> new SimpleDateFormat("dd/MM/yyyy HH:MM").format(x.getAddedAt())).toList();
    List<Double> score = results.stream()
        .map((x) -> Double.parseDouble(FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
        .toList();
    chart.addSeries(header, timestamps, score);
    return writeChartToPng(chart, header);
  }

  /**
   * Converts the chart to a PNG image and returns the path to it.
   * 
   * @param chart the chart to be converted to an PNG image
   * @param fileName the name of the PNG file
   * @return the path of the PNG image of the bar chart
   */
  private String writeChartToPng(CategoryChart chart, String fileName) throws IOException {
    BitmapEncoder.saveBitmapWithDPI(chart, fileName, BitmapFormat.PNG, 300);
    return fileName + ".png";

  }
}
