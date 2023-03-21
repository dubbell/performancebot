package com.icetlab.benchmark_worker.configuration;

import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

/**
 * Interface representing possible configurations for the repositories that are going to be benchmarked.
 */
public interface Configuration
{
    String benchmark() throws Exception;
}
