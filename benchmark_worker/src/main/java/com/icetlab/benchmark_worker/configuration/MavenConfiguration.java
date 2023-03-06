package com.icetlab.benchmark_worker.configuration;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class MavenConfiguration implements Configuration
{

    @Override
    public void compile() throws Exception
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
        // runs benchmark and creates json file for results
        Runtime.getRuntime().exec("java -jar ./benchmark_directory/target/benchmarks.jar -rf json").waitFor();
        byte[] encoded = Files.readAllBytes(Paths.get("jmh-result.json")); // reads from json file
        String result = new String(encoded, StandardCharsets.UTF_8).trim();
        new File("jmh-result.json").delete(); // deletes json file
        return result;
    }
}
