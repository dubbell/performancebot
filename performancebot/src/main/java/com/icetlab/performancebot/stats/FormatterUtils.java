package com.icetlab.performancebot.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.Method;

public class FormatterUtils {
  /**
   * Groups methods by class name. The class name is the second to last part of the method name.
   * 
   * @param methods the methods to group
   * @return a map with the class name as key and a list of methods as value
   */
  public static Map<String, List<Method>> groupMethodsByClassName(Set<Method> methods) {
    Map<String, List<Method>> methodMap = new HashMap<>();
    for (Method method : methods) {
      String fullName = method.getMethodName();
      String[] stringParts = fullName.split("\\.");
      String className = stringParts[Math.max(stringParts.length - 2, 0)];
      List<Method> m = methodMap.get(className);
      if (m == null) {
        m = new ArrayList<>();
      }
      m.add(method);
      methodMap.put(className, m);
    }
    return methodMap;
  }

  /**
   * Gets the method name from a benchmark field.
   * 
   * @param benchmarkField the benchmark field
   * @return the method name
   */
  public static String getMethodNameFromBenchmarkField(String benchmarkField) {
    String[] stringParts = benchmarkField.split("\\.");
    return stringParts[Math.max(stringParts.length - 1, 0)];
  }

  /**
   * Gets the max and min from a run result as part of a markdown table.
   * 
   * @param runResult the run result
   * @return the max and min as part of a markdown table <code> max | min |</code>
   */
  public static String getMinMaxFromPercentiles(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      JsonNode percentiles = node.get("primaryMetric").get("scorePercentiles");
      double max = percentiles.get("100.0").asDouble();
      double min = percentiles.get("0.0").asDouble();
      return String.format(" %.2f | %.2f |", min, max);
    } catch (JsonProcessingException e) {
      return " N/A | N/A |";
    }
  }

  /**
   * Gets the score and unit from a run result as part of a markdown table.
   * 
   * @param runResult the run result
   * @return the score and unit as part of a markdown table <code> score | unit |</code>
   */
  public static String getScoreAndUnitFromRunResult(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      double score = node.get("primaryMetric").get("score").asDouble();
      String unit = node.get("primaryMetric").get("scoreUnit").asText();
      return String.format(" %.2f | %s |", score, unit);
    } catch (JsonProcessingException e) {
      return " N/A | N/A |";
    }
  }
}
