package com.icetlab.performancebot.database.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.repository.BenchmarkRepository;

@Controller
public class BenchmarkController {

  @Autowired
  private BenchmarkRepository benchmarkRepository;

  public Benchmark getBenchmarkById(String id) {
    return benchmarkRepository.findById(id).orElse(null);
  }

  public void addBenchmark(String runData, String projectId) {
    benchmarkRepository.insert(new Benchmark(benchmarkRepository.count() + "", runData, projectId));
  }
}
