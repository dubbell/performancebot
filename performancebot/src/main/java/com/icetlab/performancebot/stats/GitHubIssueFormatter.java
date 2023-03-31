package com.icetlab.performancebot.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.service.InstallationService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubIssueFormatter implements BenchmarkIssueFormatter {
  @Autowired
  InstallationService installationService;

  /**
   * Formats the benchmark results into a GitHub issue. The string needs to be in JSON format.
   * 
   * @param jmhResults the results from JMH including <code>installation_id</code> and
   *        <code>repo_id</code>
   */
  @Override
  public String formatBenchmarkIssue(String jmhResults) {
    JsonNode node;
    try {
      node = new ObjectMapper().readTree(jmhResults);
      String installationId = node.get("installation_id").asText();
      String repoId = node.get("repo_id").asText();
      Set<Method> methods = installationService.getMethodsFromRepo(installationId, repoId);

      Map<String, List<Method>> methodMap = FormatterUtils.groupMethodsByClassName(methods);
      List<String> markdownTables = new ArrayList<>();
      for (String className : methodMap.keySet()) {
        markdownTables.add(getMarkdownTableFromMap(className, methodMap.get(className)));
      }
      String ret = String.format("%s\n", String.join("\n", markdownTables));
      return ret;

    } catch (JsonProcessingException e) {
      return e.toString();
    }
  }

  private String getMarkdownTableFromMap(String className, List<Method> methods) {
    String headerOne = String.format("# %s\n\n", className);
    StringBuilder sb = new StringBuilder();
    sb.append(headerOne);
    for (Method method : methods) {
      String headerTwo = String.format("## %s\n\n",
          FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName()));

      // Create table
      String topRow = """
          | Timestamp |  Min  | Max | Score | Unit |
          |-----------|-------|-----|-------|------|
          """;
      List<String> resultRows = new ArrayList<>();
      String timestamp, maxmin, scoreunit;
      Date date;

      for (int i = 0; i < Math.min(10, method.getRunResults().size()); i++) {
        Result res = method.getRunResults().get(i);
        date = res.getAddedAt();
        timestamp = new SimpleDateFormat("dd/MM/yyyy HH:MM |").format(date);
        maxmin = FormatterUtils.getMaxMinFromPercentiles(res.getData());
        scoreunit = FormatterUtils.getScoreAndUnitFromRunResult(res.getData());
        resultRows.add(String.format("| %s %s %s\n", timestamp, maxmin, scoreunit));
      }

      String resultRowsString = String.join("", resultRows).replace("[", "").replace("]", "");

      sb.append(headerTwo).append(topRow).append(resultRowsString).append("\n");
    }
    return sb.toString();
  }



}
