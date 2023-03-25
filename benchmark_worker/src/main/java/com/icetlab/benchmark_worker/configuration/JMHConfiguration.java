package com.icetlab.benchmark_worker.configuration;

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
    final protected Options options;

    public JMHConfiguration(ConfigData configData) {
        options = getOptions(configData);
    }

    /**
     * Compiles the repository.
     */
    abstract void compile() throws Exception;

    /**
     * @return the path to the compiled .class files containing the JMH benchmarks.
     */
    abstract String getClassPath();

    /**
     * @return the path to the BenchmarkList and CompilerHints files, which are required for JMH to find the tests.
     */
    abstract String getBenchmarkListPath();


    @Override
    public String benchmark() throws Exception
    {
        // compile project
        compile();

        // add classes from compiled repository so that JMH can find them
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
        return outputStream.toString(StandardCharsets.UTF_8).trim();
    }

    /**
     * Gets the contents from the ConfigData object and uses it to create a JMH Options object.
     */
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
     * Previous class path.
     */
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

        // windows uses ; as separator for class path, linux uses :
        String separator = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";

        // adds classes from repository to class path
        String newClasses = oldClasses + separator + getClassPath();
        System.setProperty("java.class.path", newClasses);

        copyBenchmarkList();
    }

    /**
     * Copies BenchmarkList and CompilerHints from repository so that JMH knows which tests to run.
     */
    private void copyBenchmarkList() throws IOException
    {
        File META_INF = new File(getBenchmarkListPath());
        File to  = new File("target/classes/META-INF");

        FileUtils.copyDirectory(META_INF, to);
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
}
