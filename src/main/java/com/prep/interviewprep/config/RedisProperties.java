package com.prep.interviewprep.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisProperties {

  private String host;
  private int port;
  private String password;
  private boolean ssl;
  private java.time.Duration timeout;

}