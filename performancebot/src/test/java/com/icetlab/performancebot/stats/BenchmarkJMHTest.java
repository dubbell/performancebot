package com.icetlab.performancebot.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.stats.generated.BenchmarkJMH;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkJMHTest {
  private static String json;
  private static ObjectMapper objectMapper;
  private static BenchmarkJMH benchmarkJMH;

  @BeforeEach
  void setup() throws JsonProcessingException {
    json = "{\"jmhVersion\":\"1.21\",\"benchmark\":\"com.mkyong.benchmark.BenchmarkLoop.loopFor\","
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
    objectMapper = new ObjectMapper();
    benchmarkJMH = objectMapper.readValue(json, BenchmarkJMH.class);

  }

  // TODO: Tests (forks, jvm version, so on)
  // TODO: Checking all scores

  /**
   * Checks amount of forks
   */
  @Test
  void checkForks() {
    Assertions.assertEquals(2, benchmarkJMH.getForks());
  }

  /**
   * Checks JMH version
   */
  @Test
  void checkJMHVersion() {
    Assertions.assertEquals("1.21", benchmarkJMH.getJmhVersion());
  }

  /**
   * Checks score
   */
  @Test
  void checkScore() {
    Assertions.assertEquals(2.82728899304076E-5, benchmarkJMH.getPrimaryMetric().getScore());
  }

  /**
   * Checks score error
   */
  @Test
  void checkScoreError() {
    Assertions.assertEquals(9.618587736247513E-8, benchmarkJMH.getPrimaryMetric().getScoreError());
  }

  /**
   * Checks score confidence (this might not work)
   */
  @Test
  void checkScoreConfidence() {
    List<Double> list = new ArrayList<>();
    list.add(2.8176704053045125E-5);
    list.add(2.8369075807770077E-5);
    Assertions.assertEquals(list, benchmarkJMH.getPrimaryMetric().getScoreConfidence());
  }

}
// TODO: More score checkers
