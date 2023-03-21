package com.icetlab.benchmark_worker.configuration;


import org.apache.commons.io.FileUtils;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GradleConfiguration implements Configuration
{
    final protected Options options;
    final List<String> buildTasks;
    final String classPath;


    public GradleConfiguration(ConfigData configData) {
        options = getOptions(configData);
        buildTasks = configData.getGradleBuildTasks();
        classPath = configData.getGradleClassPath();
    }

    @Override
    public String benchmark() throws Exception
    {
        compile();

        addClasses();

        ByteArrayOutputStream outputStream;

        try
        {
            System.out.println("Benchmarking started.");

            // performs benchmark
            Collection<RunResult> results = new Runner(options).run();

            System.out.println("Benchmarking finished.");

            // for collecting benchmark output
            outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            ResultFormat resultFormat = ResultFormatFactory.getInstance(ResultFormatType.JSON, printStream); // json output

            // writes output to output stream
            resultFormat.writeOut(results);
        }
        finally {
            // reset class path
            removeClasses();
        }

        // return result json string
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    Options getOptions(ConfigData configData) {
        // for building options object
        ChainedOptionsBuilder builder = new OptionsBuilder();

        // include every specified regex from the configuration data
        if (configData.getInclude() != null)
            for (String incl : configData.getInclude())
                builder = builder.include(incl);

        if (configData.getExclude() != null)
            for (String excl : configData.getExclude())
                builder = builder.exclude(excl);

        // number of times to run benchmarks
        if (configData.getForks() > 0)
            builder = builder.forks(configData.getForks());
        else
            builder = builder.forks(1);

        // silent verbose mode so that it doesn't spam console
        builder = builder.verbosity(VerboseMode.SILENT);

        return builder.build();
    }

    /**
     * Compile gradle project.
     */
    private void compile() {
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


    private String oldClasses = "";

    /**
     * Adds benchmark classes from repository to system class path so that JMH can find them.
     */
    private void addClasses() throws Exception {
        // makes jmh use classes from class path
        if (!Boolean.getBoolean("jmh.separateClassLoader"))
            System.setProperty("jmh.separateClassLoader", "true");

        // save old class path
        oldClasses = System.getProperty("java.class.path");

        // adds classes from repository to class path
        if(classPath == null)
            System.setProperty("java.class.path", oldClasses + ";benchmark_directory/build/classes/java/jmh");
        else
            System.setProperty("java.class.path", oldClasses + ";benchmark_directory/" + classPath);

        copyBenchmarkList();
    }

    /**
     * Resets class path to previous value.
     */
    private void removeClasses() {
        System.setProperty("java.class.path", oldClasses);

        try {
            FileUtils.deleteDirectory(new File("target/classes/META-INF"));
        } catch (Exception ignored) {}
    }

    /**
     * Copies BenchmarkList and CompilerHints from repository so that JMH knows which tests to run.
     */
    private void copyBenchmarkList() throws IOException
    {
        File META_INF = new File("benchmark_directory/build/classes/java/jmh/META-INF");
        File to  = new File("target/classes/META-INF");

        FileUtils.copyDirectory(META_INF, to);
    }
}
