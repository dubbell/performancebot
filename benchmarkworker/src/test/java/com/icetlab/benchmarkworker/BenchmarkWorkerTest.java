package com.icetlab.benchmarkworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icetlab.benchmarkworker.configuration.ConfigData;
import com.icetlab.benchmarkworker.configuration.Configuration;
import com.icetlab.benchmarkworker.configuration.ConfigurationFactory;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class BenchmarkWorkerTest {

  static BenchmarkWorker worker = new BenchmarkWorker();

  @BeforeAll
  public static void jmhSetup() {
    System.setProperty("jmh.ignoreLock", "true");
  }

  @Test
  public void mavenTest() {

    worker = new BenchmarkWorker();

    // cloning
    try {
      worker.clone("https://github.com/dubbell/JMHExample", "");
      File repoDir = new File("benchmark_directory");
      assertTrue(repoDir.isDirectory() && repoDir.listFiles().length != 0);
    } catch (Exception e) {
      fail("Cloning error : " + e);
    }

    Configuration config = null;

    // check that it can find the config file
    assertTrue(new File("benchmark_directory/perfbot.yaml").exists());

    // configuration
    try {
      ConfigData configData = new ObjectMapper(new YAMLFactory())
          .readValue(new File("benchmark_directory/perfbot.yaml"), ConfigData.class);
      assertTrue(configData.getLanguage().equalsIgnoreCase("java"));
      assertTrue(configData.getBuildTool().equalsIgnoreCase("maven"));

      config = ConfigurationFactory.getConfiguration();
    } catch (Exception e) {
      fail("Configuration error : " + e);
    }

    // benchmarking
    try {
      File target = new File("benchmark_directory/target");
      String result = config.benchmark();

      System.out.println(result);

      assertTrue(new File("benchmark_directory/target/classes/META-INF").exists());

      assertTrue(result.length() > 3); // 3 if empty list, in which case no tests were run

      // check if project was compiled correctly
      assertTrue(target.exists() && target.listFiles().length != 0);
      // check if a result was returned from the benchmark
      assertTrue(result.length() != 0);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Benchmarking error : " + e);
    }
  }

  @Test
  public void gradleExampleTest() {
    gradleTest("https://github.com/dubbell/JMHExample_Gradle");
  }

  @Test
  public void reactiveJavaTest() {
    gradleTest("https://github.com/dubbell/RxJava");
  }

  public void gradleTest(String url) {

    worker = new BenchmarkWorker();

    // cloning
    try {
      worker.clone(url, "");
      File repoDir = new File("benchmark_directory");
      assertTrue(repoDir.isDirectory() && repoDir.listFiles().length != 0);
    } catch (Exception e) {
      fail("Cloning error : " + e);
    }

    Configuration config = null;

    // check that it can find the config file
    assertTrue(new File("benchmark_directory/perfbot.yaml").exists());

    // configuration
    try {
      ConfigData configData = new ObjectMapper(new YAMLFactory())
          .readValue(new File("benchmark_directory/perfbot.yaml"), ConfigData.class);
      assertTrue(configData.getBuildTool().equalsIgnoreCase("gradle"));

      config = ConfigurationFactory.getConfiguration();
    } catch (Exception e) {
      fail("Configuration error : " + e);
    }

    // benchmarking
    try {
      File target = new File("benchmark_directory/build");
      String result = config.benchmark();

      //System.out.println(result);

      assertTrue(result.length() > 3); // 3 if empty list, in which case no tests were run

      // check if project was compiled correctly
      assertTrue(target.exists() && target.listFiles().length != 0);
      // check if a result was returned from the benchmark
      assertTrue(result.length() != 0);
    } catch (Exception e) {
      fail("Benchmarking error : " + e);
    }
  }



  @AfterEach
  public void deleteFiles() {
    worker.delete();
  }


}
