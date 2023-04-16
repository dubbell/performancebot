package com.icetlab.benchmarkworker.configuration;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.shared.invoker.*;
import java.io.*;
import java.util.Collections;

/**
 * Class representing repositories that uses Maven as the build tool, and JMH as the benchmarking
 * framework.
 */
public class MavenConfiguration extends JMHConfiguration {
  public MavenConfiguration(ConfigData configData) {
    super(configData);
  }

  /**
   * Compiles the Maven project using the Maven invoker. Executes "clean" and then "verify".
   * 
   * @throws Exception
   */
  @Override
  protected void compile() throws Exception {
    System.out.println("Compilation started.");

    List<InvocationRequest> mavenInvocations = new ArrayList<>();

    for (BuildTask task : configData.getBuildTasks()) {
      InvocationRequest request = new DefaultInvocationRequest();
      request.setPomFile(new File("benchmark_directory" + task.getPath()));
      request.setGoals(task.getTasks());
      request.setQuiet(true);
      request.setInputStream(InputStream.nullInputStream());
      mavenInvocations.add(request);
    }

    // cleans and then compiles project
    Invoker invoker = new DefaultInvoker();
    if (System.getProperty("os.name").toLowerCase().contains("win")) // if windows
      invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));

    int exitCodeSum = 0;

    for(InvocationRequest request : mavenInvocations) {
      int exitCode = invoker.execute(request).getExitCode();
      System.out.println("Maven task executed with exit code: " + exitCode);
      exitCodeSum += exitCode;
    }

    System.out.println("Compilation finished.");

    // checks if any of the requests failed
    if (exitCodeSum > 0)
      throw new Exception("Build failed.");
  }

  @Override
  String getJmhJar() {
    return "benchmark_directory/target/benchmarks.jar";
  }


}
