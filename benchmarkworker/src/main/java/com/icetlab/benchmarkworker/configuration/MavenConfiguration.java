package com.icetlab.benchmarkworker.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.maven.shared.invoker.*;
import java.io.*;

/**
 * Class representing repositories that uses Maven as the build tool, and JMH as
 * the benchmarking
 * framework.
 */
public class MavenConfiguration extends JMHConfiguration {
  public MavenConfiguration(ConfigData configData) {
    super(configData);
  }

  /**
   * Compiles the Maven project using the Maven invoker. Executes "install" goal
   * using pom file in
   * the repository's root directory if no buildTasks are specified in the
   * perfbot.yaml file.
   * Otherwise executes the goals in the file.
   * 
   * @throws Exception
   */
  @Override
  protected void compile() throws Exception {
    System.out.println("Compilation started.");

    List<InvocationRequest> mavenInvocations = new ArrayList<>();

    if (configData.getBuildTasks() == null) {
      InvocationRequest request = new DefaultInvocationRequest();
      request.setPomFile(new File("benchmark_directory/pom.xml"));
      request.setGoals(Collections.singletonList("package"));
      request.setQuiet(true);
      request.setInputStream(InputStream.nullInputStream());
      mavenInvocations.add(request);
    } else {
      for (BuildTask task : configData.getBuildTasks()) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("benchmark_directory/" + task.getPath()));
        request.setGoals(task.getTasks());
        request.setQuiet(true);
        request.setInputStream(InputStream.nullInputStream());

        Properties properties = new Properties();
        properties.setProperty("skipTests", "true"); // skip tests as they take too much time
        request.setProperties(properties);

        mavenInvocations.add(request);
      }
    }

    Invoker invoker = new DefaultInvoker();
    if (System.getProperty("os.name").toLowerCase().contains("win")
        || System.getProperty("os.name").toLowerCase().contains("mac")) // if windows
      invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));

    for (InvocationRequest request : mavenInvocations) {
      int exitCode = invoker.execute(request).getExitCode();
      System.out.println("Maven task executed with exit code: " + exitCode);
    }

    System.out.println("Compilation finished.");
  }

  @Override
  String getJmhJar() {
    return "benchmark_directory/target/benchmarks.jar";
  }

}
