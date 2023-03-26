package com.icetlab.performancebot.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class GitHubConfig {

  @Value("${github.app_id}")
  private String appId;

  @Value("${github.private_key_path}")
  private String privateKeyPath;

  public String getAppId() {
    return appId;
  }

  public String getPrivateKeyPath() {
    return privateKeyPath;
  }
}
