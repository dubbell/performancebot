package com.icetlab.benchmarkworker.configuration;


/**
 * Interface representing possible configurations for the repositories that are going to be
 * benchmarked.
 */
public interface Configuration {
  String benchmark() throws Exception;
}
