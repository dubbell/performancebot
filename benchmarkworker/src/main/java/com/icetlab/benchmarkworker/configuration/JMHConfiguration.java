package com.icetlab.benchmarkworker.configuration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

/**
 * Superclass for repositories that use JMH for performance testing.
 */
public abstract class JMHConfiguration implements Configuration {

  /**
   * JMH Options, gets set by the values in the configuration file.
   */
  final protected ConfigData configData;

  public JMHConfiguration(ConfigData configData) {
    this.configData = configData;
  }

  /**
   * Compiles the repository.
   */
  abstract void compile() throws Exception;

  /**
   * Path to compiled JMH jar file.
   */
  abstract String getJmhJar();

  @Override
  public String benchmark() throws Exception {

    // compile project
    compile();

    String result;

    try {

      System.out.println("Benchmarking started.");

      // runs benchmarks
      Process benchmarkingProcess = Runtime.getRuntime().exec(getJmhCommand().split("[ \t]+"));
      benchmarkingProcess.getInputStream().close(); // gets stuck at waitFor() otherwise.
      benchmarkingProcess.waitFor();

      // reads everything from result file
      result = new String(Files.readAllBytes(Paths.get("jmh-result.json")));

      System.out.println("Benchmarking finished.");

    } finally {
      new File("jmh-result.json").delete();
    }

    return result.trim();
  }

  /**
   * Command to run benchmarks.
   */
  private String getJmhCommand() {
    String jar = configData.getJmhJar() == null ? getJmhJar()
        : "benchmark_directory/" + configData.getJmhJar();
    String options = configData.getOptions() == null ? "" : configData.getOptions();
    return "java -jar " + jar + " " + options + " -rf json";
  }

}
