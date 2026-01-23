package com.prep.interviewprep.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {

    RedisStandaloneConfiguration redisConfig =
        new RedisStandaloneConfiguration(
            redisProperties.getHost(),
            redisProperties.getPort()
        );
    redisConfig.setPassword(redisProperties.getPassword());

    LettuceClientConfiguration clientConfig =
        LettuceClientConfiguration.builder()
            .useSsl()
            .and()
            .commandTimeout(java.time.Duration.ofSeconds(10))
            .build();

    return new LettuceConnectionFactory(redisConfig, clientConfig);
  }

  @Bean
  public RedisTemplate<String, Long> redisTemplate(
      LettuceConnectionFactory connectionFactory) {

    RedisTemplate<String, Long> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());

    return template;
  }
}
