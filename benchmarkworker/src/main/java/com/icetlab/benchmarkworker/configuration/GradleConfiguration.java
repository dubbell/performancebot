package com.icetlab.benchmarkworker.configuration;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import java.io.File;
import java.util.List;

/**
 * Class representing repositories that uses Gradle as the build tool, and JMH as the benchmarking
 * framework.
 */
public class GradleConfiguration extends JMHConfiguration {

  public GradleConfiguration(ConfigData configData) {
    super(configData);
  }

  /**
   * Compiles gradle project using the Gradle API. Runs the jmhJar gradle task by default if no buildTasks are specified
   * in the perfbot.yaml file.
   */
  protected void compile() {
    System.out.println("Compilation started.");

    ProjectConnection connection = null;

    try {
      if (configData.getBuildTasks() == null) { // default build task
        connection = GradleConnector.newConnector()
            .forProjectDirectory(new File("benchmark_directory")).connect();

        connection.newBuild().forTasks("jmhJar").run();
      }
      else {
        for(BuildTask task : configData.getBuildTasks()) {
          connection = GradleConnector.newConnector()
              .forProjectDirectory(new File("benchmark_directory/" + task.getPath())).connect();

          BuildLauncher buildLauncher = connection.newBuild();
          for (String t : task.getTasks()) {
            buildLauncher = buildLauncher.forTasks(t);
          }

          buildLauncher.run();
          connection.close();
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (connection != null)
          connection.close();
      } catch (Exception ignored) {}
      System.out.println("Compilation finished.");
    }
  }

  @Override
  String getJmhJar() {

    File libsFolder = new File("benchmark_directory/build/libs");
    for(File file : libsFolder.listFiles())
      if(file.getName().toLowerCase().contains("jmh"))
        return "benchmark_directory/build/libs/" + file.getName();

    return "";
  }
}

