package com.icetlab.performancebot.stats;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
      List<String> methodNames = FormatterUtils.getMethodsFromCurrentRun(jmhResults);
      Set<Method> methods = installationService.getMethodsFromRepo(installationId, repoId).stream()
          .filter(method -> methodNames.contains(method.getMethodName()))
          .collect(Collectors.toSet());
      Map<String, List<Method>> methodMap = FormatterUtils.groupMethodsByClassName(methods);
      for (String className : methodMap.keySet()) {
        createCategoryChartFromMap(className, methodMap.get(className));
      }
      return "TODO: MD formatted string.";
    } catch (JsonProcessingException e) {
      return e.toString();
    }
  }

  private void createCategoryChartFromMap(String className, List<Method> methods) {
    List<Result> results = new ArrayList<>();
    for (Method method : methods) {
      method.getRunResults().forEach((x) -> results.add(x));
      createImageFromResults(results, method.getMethodName());
      results.clear();
    }
  }

  private void createImageFromResults(List<Result> results, String header) {
    CategoryChart chart = new CategoryChartBuilder().width(600).height(600).title(header)
        .xAxisTitle("Date").yAxisTitle("Score").build();
    List<String> timestamps = results.stream()
        .map((x) -> new SimpleDateFormat("dd/MM/yyyy HH:MM").format(x.getAddedAt())).toList();
    List<Double> score = results.stream()
        .map((x) -> Double.parseDouble(FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
        .toList();
    chart.addSeries(header, timestamps, score);
    writeChartToPng(chart, header);
  }

  private void writeChartToPng(CategoryChart chart, String path) {
    try {
      BitmapEncoder.saveBitmapWithDPI(chart, "./" + path, BitmapFormat.PNG, 300);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
