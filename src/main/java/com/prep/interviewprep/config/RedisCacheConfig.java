package com.prep.interviewprep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.prep.interviewprep.dto.FiltersResponse;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisCacheConfig {

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

    ObjectMapper objectMapper = JsonMapper.builder()
        .findAndAddModules()
        .build();

    Jackson2JsonRedisSerializer<FiltersResponse> serializer =
        new Jackson2JsonRedisSerializer<>(FiltersResponse.class);
    serializer.setObjectMapper(objectMapper); // yes, deprecated but REQUIRED here

    RedisCacheConfiguration filtersCacheConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            );

    return RedisCacheManager.builder(connectionFactory)
        .withCacheConfiguration("metadataFilters:v3", filtersCacheConfig)
        .build();
  }
}



