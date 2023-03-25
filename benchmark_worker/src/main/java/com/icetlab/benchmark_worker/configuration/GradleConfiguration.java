package com.icetlab.benchmark_worker.configuration;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import java.io.File;
import java.util.List;

/**
 * Class representing repositories that uses Gradle as the build tool, and JMH as the benchmarking framework.
 */
public class GradleConfiguration extends JMHConfiguration
{
    final List<String> buildTasks;
    final String classPath;

    public GradleConfiguration(ConfigData configData) {
        super(configData);

        buildTasks = configData.getGradleBuildTasks();
        classPath = configData.getGradleClassPath();
    }

    /**
     * Compiles gradle project using the Gradle API.
     */
    protected void compile() {
        System.out.println("Compilation started.");
        ProjectConnection connection = GradleConnector
                .newConnector()
                .forProjectDirectory(new File("benchmark_directory"))
                .connect();

        try {
            if (buildTasks == null)
                connection.newBuild().forTasks("clean", "assemble").run();
            else
            {
                BuildLauncher bl = connection.newBuild();
                for (String task : buildTasks)
                    bl = bl.forTasks(task);
                bl.run();
            }
        }
        finally {
            connection.close();
            System.out.println("Compilation finished.");
        }
    }

    @Override
    String getClassPath() {
        if(classPath == null)
            return "benchmark_directory/build/classes/java/jmh";
        else
            return "benchmark_directory/" + classPath;
    }

    @Override
    String getBenchmarkListPath() {
        return getClassPath() + "/META-INF";
    }


}

