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
import java.util.stream.Collectors;

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

  public static String getMinFromPercentiles(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      JsonNode percentiles = node.get("primaryMetric").get("scorePercentiles");
      Double min = percentiles.get("0.0").asDouble();
      return min.toString();
    } catch (JsonProcessingException e) {
      return "N/A";
    }
  }

  public static String getMaxFromPercentiles(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      JsonNode percentiles = node.get("primaryMetric").get("scorePercentiles");
      Double min = percentiles.get("100.0").asDouble();
      return min.toString();
    } catch (JsonProcessingException e) {
      return "N/A";
    }
  }

  public static String getScoreFromPrimaryMetric(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      Double score = node.get("primaryMetric").get("score").asDouble();
      return score.toString();
    } catch (JsonProcessingException e) {
      return "N/A";
    }
  }

  public static String getUnitFromPrimaryMetric(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      String unit = node.get("primaryMetric").get("scoreUnit").asText();
      return unit.toString();
    } catch (JsonProcessingException e) {
      return "N/A";
    }
  }

  public static String getMeasurementIterations(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      String mIterations = node.get("measurementIterations").asText();
      return mIterations;
    } catch (JsonProcessingException e) {
      return "N/A";
    }
  }

  public static String getMode(String runResult) {
    try {
      JsonNode node = new ObjectMapper().readTree(runResult);
      String mode = node.get("mode").asText();
      return mode;
    } catch (JsonProcessingException e) {
      return "N/A";
    }
  }

  public static List<String> getMethodNamesFromCurrentRun(String jmhResults) {
    JsonNode node;
    try {
      node = new ObjectMapper().readTree(jmhResults);
      List<String> methodNames = new ArrayList<>();
      methodNames = node.get("results").findValuesAsText("benchmark", methodNames);
      return methodNames;
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  public static Set<Method> filterMethodsFromCurrentRun(Set<Method> allMethods,
      List<String> methodNamesToFilterBy) {
    return allMethods.stream()
        .filter(method -> methodNamesToFilterBy.contains(method.getMethodName()))
        .collect(Collectors.toSet());
  }
}
