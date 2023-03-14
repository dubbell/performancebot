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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.Collections;


public class MavenConfiguration implements Configuration
{
    final private Options options;

    public MavenConfiguration(ConfigData configData) {
        // for building options object
        ChainedOptionsBuilder builder = new OptionsBuilder();

        // include every specified regex from the configuration data
        for (String regex : configData.getRegex())
            builder = builder.include(".*"+regex+".*");

        // silent verbose mode so that it doesn't spam console
        builder = builder.forks(1).verbosity(VerboseMode.NORMAL);

        options = builder.build();

    }

    private void compile() throws Exception
    {
        // construct request to clean target directory
        InvocationRequest cleanRequest = new DefaultInvocationRequest();
        cleanRequest.setPomFile(new File("benchmark_directory/pom.xml"));
        cleanRequest.setGoals(Collections.singletonList("clean"));

        // construct request to compile project
        InvocationRequest verifyRequest = new DefaultInvocationRequest();
        verifyRequest.setPomFile(new File("benchmark_directory/pom.xml"));
        verifyRequest.setGoals(Collections.singletonList("verify"));

        // cleans and then compiles project
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
        InvocationResult cleanResult = invoker.execute(cleanRequest);
        InvocationResult verifyResult = invoker.execute(verifyRequest);

        // checks if either of the requests failed
        if (cleanResult.getExitCode() != 0 || verifyResult.getExitCode() != 0)
            throw new Exception("Build failed.");
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
            // performs benchmark
            Collection<RunResult> results = new Runner(options).run();

            // for collecting benchmark output
            outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            ResultFormat resultFormat = ResultFormatFactory.getInstance(ResultFormatType.JSON, printStream); // json output

            // writes output to output stream
            resultFormat.writeOut(results);
        }
        catch (Exception e) {
            removeClasses();
            throw new Exception(e);
        }

        // reset class path
        removeClasses();

        // return result json string
        return outputStream.toString(StandardCharsets.UTF_8);
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
        //if (!Boolean.getBoolean("jmh.separateClassLoader"))
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

        try
        {
            FileUtils.deleteDirectory(new File("target/classes/META-INF"));
        } catch (Exception ignored) {}
    }
}
