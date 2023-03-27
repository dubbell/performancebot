package com.icetlab.benchmarkworker.configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormat;
import org.openjdk.jmh.results.format.ResultFormatFactory;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

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
      Runtime.getRuntime().exec(getJmhCommand().split("[ \t]+")).waitFor();

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
    String jar = configData.getJmhJar() == null ? getJmhJar() : configData.getJmhJar();
    String options = configData.getOptions() == null ? "" : configData.getOptions();
    return "java -jar " + jar + " " + options + "-rf json";
  }

}
