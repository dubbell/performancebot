package com.icetlab.performancebot.client;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.icetlab.performancebot.Config;

/**
 * Client for benchmarkworker that uses localhost as the server address
 */
public class Localhost implements BenchmarkWorkerClient {

  private String hostIp;

  public Localhost(int port) {
    hostIp = "localhost:" + port;
  }

  public Localhost() {
    hostIp = loadServerIpFromProperties();
  }

  private static String loadServerIpFromProperties() {
    try (AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(Config.class)) {
      Config config = context.getBean(Config.class);
      String ip = tryGetIpFromProperties(config);
      String port = tryGetPortFromProperties(config);
      return ip + ":" + port;
    }
  }

  private static String tryGetIpFromProperties(Config conf) {
    try {
      return conf.getServerIp();
    } catch (BeanCreationException e) {
      return "localhost";
    }
  }

  private static String tryGetPortFromProperties(Config conf) {
    try {
      return conf.getServerPort();
    } catch (BeanCreationException e) {
      return "8080";
    }
  }

  /**
   * Get the configured server IP, defaults to localhost:8081
   */
  @Override
  public String getServerIp() {
    return new String(hostIp);
  }
}
