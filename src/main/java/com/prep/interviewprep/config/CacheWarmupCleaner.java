package com.prep.interviewprep.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheWarmupCleaner {

  private final CacheManager cacheManager;

  @EventListener(ApplicationReadyEvent.class)
  public void clearPoisonedCache() {
    Cache cache = cacheManager.getCache("metadataFilters");
    if (cache != null) {
      cache.clear();
    }
  }
}
