package com.icetlab.performancebot.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import org.springframework.stereotype.Component;

@Component
public class BenchmarkTable implements VisualizationStrategy {

  @Override
  public String visualizeBenchmarkResults(Method method) {
    StringBuilder sb = new StringBuilder();
    String headerTwo = String.format("## %s\n\n",
        FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName()));

    // Create table
    String topRow = """
        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|
        """;
    List<String> resultRows = new ArrayList<>();

    String timestamp;
    Date date;

    for (int i = 0; i < Math.min(10, method.getRunResults().size()); i++) {
      Result res = method.getRunResults().get(i);
      date = res.getAddedAt();
      timestamp = new SimpleDateFormat("dd/MM/yyyy HH:MM").format(date);
      String min = FormatterUtils.getMinFromPercentiles(res.getData());
      String max = FormatterUtils.getMaxFromPercentiles(res.getData());
      String score = FormatterUtils.getScoreFromPrimaryMetric(res.getData());
      String unit = FormatterUtils.getUnitFromPrimaryMetric(res.getData());
      resultRows
          .add(String.format("| %s | %s | %s | %s | %s |\n", timestamp, max, min, score, unit));
    }

    String resultRowsString = String.join("", resultRows).replace("[", "").replace("]", "");

    sb.append(headerTwo).append(topRow).append(resultRowsString).append("\n");
    return sb.toString();
  }


}
