package com.icetlab.performancebot.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IssueLoggerTest {

  private static String validJson =
      "{\"jmhVersion\":\"1.21\",\"benchmark\":\"com.mkyong.benchmark.BenchmarkLoop.loopFor\","
          + "\"mode\":\"avgt\",\"threads\":1,\"forks\":2,"
          + "\"jvm\":\"/opt/homebrew/Cellar/openjdk@17/17.0.6/libexec/openjdk.jdk/Contents/Home/bin/java\","
          + "\"jvmArgs\":[\"-Xms2G\",\"-Xmx2G\"],\"jdkVersion\":\"17.0.6\",\"vmName\":\"OpenJDK 64-Bit Server VM\","
          + "\"vmVersion\":\"17.0.6+0\",\"warmupIterations\":2,\"warmupTime\":\"10 s\","
          + "\"warmupBatchSize\":1,\"measurementIterations\":2,\"measurementTime\":\"10 s\","
          + "\"measurementBatchSize\":1,\"params\":{\"N\":\"10\"},"
          + "\"primaryMetric\":{\"score\":2.82728899304076E-5,\"scoreError\":9.618587736247513E-8,"
          + "\"scoreConfidence\":[2.8176704053045125E-5,2.8369075807770077E-5],"
          + "\"scorePercentiles\":{\"0.0\":2.8259540290990172E-5,"
          + "\"50.0\":2.8272023951704223E-5,\"90.0\":2.828797152723178E-5,"
          + "\"95.0\":2.828797152723178E-5,\"99.0\":2.828797152723178E-5,"
          + "\"99.9\":2.828797152723178E-5,\"99.99\":2.828797152723178E-5,"
          + "\"99.999\":2.828797152723178E-5,\"99.9999\":2.828797152723178E-5,"
          + "\"100.0\":2.828797152723178E-5}," + "\"scoreUnit\":\"ms/op\","
          + "\"rawData\":[[2.828797152723178E-5,2.828337099713185E-5],[2.8259540290990172E-5,2.8260676906276593E-5]]},"
          + "\"secondaryMetrics\":{}}";

  private static String jsonMissingScore =
      "{\"jmhVersion\":\"1.21\",\"benchmark\":\"com.mkyong.benchmark.BenchmarkLoop.loopFor\","
          + "\"mode\":\"avgt\",\"threads\":1,\"forks\":2,"
          + "\"jvm\":\"/opt/homebrew/Cellar/openjdk@17/17.0.6/libexec/openjdk.jdk/Contents/Home/bin/java\","
          + "\"jvmArgs\":[\"-Xms2G\",\"-Xmx2G\"],\"jdkVersion\":\"17.0.6\",\"vmName\":\"OpenJDK 64-Bit Server VM\","
          + "\"vmVersion\":\"17.0.6+0\",\"warmupIterations\":2,\"warmupTime\":\"10 s\","
          + "\"warmupBatchSize\":1,\"measurementIterations\":2,\"measurementTime\":\"10 s\","
          + "\"measurementBatchSize\":1,\"params\":{\"N\":\"10\"},"
          + "\"primaryMetric\":{\"scoreError\":9.618587736247513E-8,"
          // score should be before scoreError
          + "\"scoreConfidence\":[2.8176704053045125E-5,2.8369075807770077E-5],"
          + "\"scorePercentiles\":{\"0.0\":2.8259540290990172E-5,"
          + "\"50.0\":2.8272023951704223E-5,\"90.0\":2.828797152723178E-5,"
          + "\"95.0\":2.828797152723178E-5,\"99.0\":2.828797152723178E-5,"
          + "\"99.9\":2.828797152723178E-5,\"99.99\":2.828797152723178E-5,"
          + "\"99.999\":2.828797152723178E-5,\"99.9999\":2.828797152723178E-5,"
          + "\"100.0\":2.828797152723178E-5}," + "\"scoreUnit\":\"ms/op\","
          + "\"rawData\":[[2.828797152723178E-5,2.828337099713185E-5],[2.8259540290990172E-5,2.8260676906276593E-5]]},"
          + "\"secondaryMetrics\":{}}";

  private static Double scoreOfValidJson = 2.82728899304076E-5;
  private static String benchmarkOfValidJson = "com.mkyong.benchmark.BenchmarkLoop.loopFor";


  // Check that valid json string's is equal to issue string returned by createSimpleIssue
  @Test
  public void testCreateSimpleIssue() throws JsonProcessingException {
    String simpleIssue = IssueLogger.createSimpleIssue(validJson);
    String expectedIssue =
        "Score of com.mkyong.benchmark.BenchmarkLoop.loopFor: 2.82728899304076E-5";
    Assertions.assertEquals(simpleIssue, expectedIssue);
  }

  // TODO: createSimpleIssue(String json) - Check that non valid json string input is handled


}
