package com.icetlab.benchmarkworker.client;

/**
 * Client for performancebot that uses localhost as the server address
 */
public class Localhost implements PerformanceBotClient {

  private String benchmarkControllerAddress;

  /**
   * Uses localhost with custom port
   */
  public Localhost(int port) {
    benchmarkControllerAddress = "http://localhost:" + port;
  }

  /**
   * Uses localhost with default port
   */
  public Localhost() {
    benchmarkControllerAddress = "http://localhost:8080";
  }

  @Override
  public String getServerIpWithPort() {
    return new String(benchmarkControllerAddress);
  }
}
