package com.icetlab.benchmarkworker.client;

public class Localhost implements Client {

  private String hostIp;

  /**
   * Uses localhost with custom port
   */
  public Localhost(int port) {
    hostIp = "localhost:" + port;
  }

  /**
   * Uses localhost IP to performancebot
   */
  public Localhost() {
    hostIp = "localhost:8080";
  }

  @Override
  public String getServerIpWithPort() {
    return new String(hostIp);
  }
}
