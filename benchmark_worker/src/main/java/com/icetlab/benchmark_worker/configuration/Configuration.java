package com.icetlab.benchmark_worker.configuration;


/**
 * Interface representing possible configurations for the repositories that are going to be benchmarked.
 */
public interface Configuration
{
    String benchmark() throws Exception;
}
