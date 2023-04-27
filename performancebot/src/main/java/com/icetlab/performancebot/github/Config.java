package com.icetlab.performancebot.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {

  @Value("${github.app_id}")
  private String appId;

  @Value("${github.private_key_path}")
  private String privateKeyPath;

  @Value("${server.ip}")
  private String serverIp;

  @Value("${server.port}")
  private String serverPort;

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
}
