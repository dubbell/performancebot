package com.icetlab.benchmark_worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icetlab.benchmark_worker.configuration.ConfigData;
import com.icetlab.benchmark_worker.configuration.MavenConfiguration;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class BenchmarkWorkerTest {

    static BenchmarkWorker worker = new BenchmarkWorker();
    static MavenConfiguration config = new MavenConfiguration();

    @Test
    public void workerTest() {
        // cloning
        try{
            worker.clone("https://github.com/dubbell/JMHExample", "");
            File repoDir = new File("benchmark_directory");
            assertTrue(repoDir.isDirectory() && repoDir.listFiles().length != 0);
        } catch (Exception e) {
            fail("Cloning error : " + e);
        }

        // check that it can find the config file
        assertTrue(new File("benchmark_directory/perfbot.yaml").exists());

        // configuration
        try {
            ConfigData configData = new ObjectMapper(new YAMLFactory()).readValue(new File("benchmark_directory/perfbot.yaml"), ConfigData.class);
            assertTrue(configData.getLanguage().equalsIgnoreCase("java"));
            assertTrue(configData.getBuildTool().equalsIgnoreCase("maven"));
        } catch (Exception e) {
            fail("Configuration error : " + e);
        }

        // compilation
        File target = new File("benchmark_directory/target");
        try {
            config.compile();
            assertTrue(target.exists() && target.listFiles().length != 0);
        } catch (Exception e) {
            fail("Compilation error : " + e);
        }

        // benchmarking
        try {
            String result = config.benchmark();
            assertTrue(result.length() != 0);
        } catch (Exception e) {
            fail("Benchmarking error : " + e);
        }


    }

    @AfterEach
    public void deleteFiles() {
        worker.delete();
    }


}
