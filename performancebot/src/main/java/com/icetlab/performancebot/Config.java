package com.icetlab.performancebot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for performancebot, which is used to load properties from
 * application.properties. These properties are used to configure the authentication key and app ID
 * for the GitHub app as well as the server IP and port of benchmarkworker.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class Config {

  @Value("${github.app_id}")
  private String appId;

  @Value("${github.private_key_path}")
  private String privateKeyPath;

  @Value("${server.bworker.ip:localhost}")
  private String serverIp;

  @Value("${server.bworker.port:8081}")
  private String serverPort;

  @Value("${imagekit.privatekey}")
  private String imageKitPrivateKey;

  public String getAppId() {
    return appId;
  }

  public String getPrivateKeyPath() {
    return privateKeyPath;
  }

  public String getServerIp() {
    return serverIp;
  }

  public String getServerPort() {
    return serverPort;
  }

  public String getImageKitPrivateKey() {
    return imageKitPrivateKey;
  }
}
