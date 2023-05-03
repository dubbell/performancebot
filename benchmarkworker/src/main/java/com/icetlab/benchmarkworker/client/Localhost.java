package com.icetlab.benchmarkworker.client;

public class Localhost implements PerformanceBotClient {

  private String benchmarkControllerAddress;

  /**
   * Uses localhost with custom port
   */
  public Localhost(int port) {
    benchmarkControllerAddress = "http://localhost:" + port;
  }

  /**
   * Uses localhost IP to performancebot
   */
  public Localhost() {
    benchmarkControllerAddress = "http://localhost:8080";
  }

  @Override
  public String getServerIpWithPort() {
    return new String(benchmarkControllerAddress);
  }
}
