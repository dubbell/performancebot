package com.icetlab.performancebot.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.controller.InstallationController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.icetlab.performancebot.stats.visualization.BenchmarkBarPlot;
import com.icetlab.performancebot.stats.visualization.VisualizationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MethodByClassFormatter implements BenchmarkIssueFormatter {

  VisualizationStrategy visualizationStrategy = new BenchmarkBarPlot(); // Change statistical analysis strategy here
  @Autowired
  private InstallationController controller;

  @Override
  public String formatBenchmarkIssue(String jmhResults) {
    JsonNode node;
    try {
      node = new ObjectMapper().readTree(jmhResults);
      String installationId = node.get("installation_id").asText();
      String repoId = node.get("repo_id").asText();
      List<String> methodNames = FormatterUtils.getMethodNamesFromCurrentRun(jmhResults);
      Set<Method> methods = FormatterUtils.filterMethodsFromCurrentRun(
          controller.getMethodsFromRepo(installationId, repoId), methodNames);
      Map<String, List<Method>> classes = FormatterUtils.groupMethodsByClassName(methods);
      return formatIssueBody(classes);
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
   * # ClassName
   * for method in methods:
   *  visualizationStrategy
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
        String runResultsVisualization = visualizationStrategy.visualizeBenchmarkResults(method);
        sb.append(runResultsVisualization);
      }
    }
    return sb.toString();
  }


}
