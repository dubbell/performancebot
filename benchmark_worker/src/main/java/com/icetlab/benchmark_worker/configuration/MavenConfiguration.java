package com.icetlab.benchmark_worker.configuration;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormat;
import org.openjdk.jmh.results.format.ResultFormatFactory;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.Collections;


public class MavenConfiguration implements Configuration
{
    final protected Options options;
    public MavenConfiguration(ConfigData configData) {

        options = getOptions(configData);
    }



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

    private void compile() throws Exception
    {
        System.out.println("Compilation started.");


        // construct request to clean target directory
        InvocationRequest cleanRequest = new DefaultInvocationRequest();
        cleanRequest.setPomFile(new File("benchmark_directory/pom.xml"));
        cleanRequest.setGoals(Collections.singletonList("clean"));
        cleanRequest.setQuiet(true);
        cleanRequest.setInputStream(InputStream.nullInputStream());

        // construct request to compile project
        InvocationRequest verifyRequest = new DefaultInvocationRequest();
        verifyRequest.setPomFile(new File("benchmark_directory/pom.xml"));
        verifyRequest.setGoals(Collections.singletonList("verify"));
        verifyRequest.setQuiet(true);
        verifyRequest.setInputStream(InputStream.nullInputStream());

        // cleans and then compiles project
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));

        InvocationResult cleanResult = invoker.execute(cleanRequest);
        System.out.println("Maven clean executed with exit code: " + cleanResult.getExitCode());

        InvocationResult verifyResult = invoker.execute(verifyRequest);
        System.out.println("Maven verify executed with exit code: " + verifyResult.getExitCode());

        System.out.println("Compilation finished.");

        // checks if either of the requests failed
        if (cleanResult.getExitCode() != 0 || verifyResult.getExitCode() != 0)
            throw new Exception("Build failed.");
    }

    /**
     * Copies BenchmarkList and CompilerHints from repository so that JMH knows which tests to run.
     */
    private void copyBenchmarkList() throws IOException
    {
        File META_INF = new File("benchmark_directory/target/classes/META-INF");
        File to  = new File("target/classes/META-INF");

        FileUtils.copyDirectory(META_INF, to);
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

        // adds classes from repository to class path
        String newClasses = oldClasses + ";benchmark_directory/target/classes";
        System.setProperty("java.class.path", newClasses);

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
}
