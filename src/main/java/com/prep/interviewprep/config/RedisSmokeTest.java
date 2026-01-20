package com.prep.interviewprep.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSmokeTest {

  private final RedisTemplate<String, String> redisTemplate;

  @PostConstruct
  public void test() {
    redisTemplate.opsForValue().set("redis-test", "OK");
    System.out.println("Redis says: " +
        redisTemplate.opsForValue().get("redis-test"));
  }
}