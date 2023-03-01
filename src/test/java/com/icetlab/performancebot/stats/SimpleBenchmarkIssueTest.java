package com.icetlab.performancebot.stats;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONStringer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
public class SimpleBenchmarkIssueTest {
    private static String json;
    private static JSONStringer jsonStringer = new JSONStringer();

    private static BenchmarkJMH[] benchmarksJMH;
    private static SimpleBenchmarkIssue benchmarkIssue;
    private static BenchmarkJMH benchmarkJMH;
    private static String issue;
    private static String[] splitIssue;
    private static ObjectMapper objectMapper;
  //TODO: Setup
    @BeforeEach
    void setup() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        json =    "{\"jmhVersion\":\"1.21\",\"benchmark\":\"com.mkyong.benchmark.BenchmarkLoop.loopFor\","
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
                + "\"100.0\":2.828797152723178E-5},"
                + "\"scoreUnit\":\"ms/op\","
                + "\"rawData\":[[2.828797152723178E-5,2.828337099713185E-5],[2.8259540290990172E-5,2.8260676906276593E-5]]},"
                + "\"secondaryMetrics\":{}}";
        benchmarksJMH = new BenchmarkJMH[1];
        benchmarkIssue = new SimpleBenchmarkIssue();
        benchmarkJMH = objectMapper.readValue(json, BenchmarkJMH.class);
        benchmarksJMH[0] = benchmarkJMH;
        issue = benchmarkIssue.formatBenchmarkIssue(benchmarksJMH);
        splitIssue = issue.split(" ");

    }

    /**
     * Checks that sthe score in JMHBenchmark is equal to score in string that s returned
     *
     */
    @Test
    public void testBenchmarkScore(){
        // should be handled by now
        Assertions.assertEquals(2.82728899304076E-5, benchmarkJMH.getPrimaryMetric().getScore());
    }

    /**
     * Tests that the returned benchmark in string is equal to benchmark in JMHBenchmark
     *
     */
    @Test
    public void testReturnedBenchmarkString(){
        //Parse issue back to benchmark, to compare
        //Unsure if "benchmark" is correct,
        Assertions.assertEquals(issue, "Score of " + "benchmark" + ": " + 2.82728899304076E-5);

    }
    /**
     * Checks that at least one Benchmark is provided for each issue
     *
     */
    @Test
    public void testEmptyBenchmarkArray(){
        //Use this test or the checker in the method
        Assertions.assertNotEquals(0, benchmarksJMH.length);
    }

}
