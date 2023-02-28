package com.icetlab.benchmark_worker;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class BenchmarkWorkerTest {

    static BenchmarkWorker worker = new BenchmarkWorker();

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

        // compilation
        File target = new File("benchmark_directory/target");
        try {
            worker.compile();
            assertTrue(target.exists() && target.listFiles().length != 0);
        } catch (Exception e) {
            fail("Compilation error : " + e);
        }

        // benchmarking
        try {
            worker.benchmark();
            File result = new File("jmh-result.json");
            assertTrue(result.exists() && worker.readResults().length() != 0);
        } catch (Exception e) {
            fail("Benchmarking error : " + e);
        }


    }

    @AfterEach
    public void deleteFiles() {
        worker.delete();
    }


}
