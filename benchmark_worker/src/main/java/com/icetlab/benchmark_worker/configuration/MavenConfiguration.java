package com.icetlab.benchmark_worker.configuration;

import org.apache.maven.shared.invoker.*;
import java.io.*;
import java.util.Collections;

/**
 * Class representing repositories that uses Maven as the build tool, and JMH as the benchmarking framework.
 */
public class MavenConfiguration extends JMHConfiguration
{
    public MavenConfiguration(ConfigData configData) {
        super(configData);
    }

    /**
     * Compiles the Maven project using the Maven invoker. Executes "clean" and then "verify".
     * @throws Exception
     */
    @Override
    protected void compile() throws Exception
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
        if(System.getProperty("os.name").toLowerCase().contains("win")) // if windows
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

    @Override
    String getClassPath() {
        return "benchmark_directory/target/classes";
    }

    @Override
    String getBenchmarkListPath() {
        return "benchmark_directory/target/classes/META-INF";
    }


}
