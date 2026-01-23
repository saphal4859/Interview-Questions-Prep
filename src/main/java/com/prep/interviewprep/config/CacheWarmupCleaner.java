package com.prep.interviewprep.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupCleaner {

  private final CacheManager cacheManager;

  @EventListener(ApplicationReadyEvent.class)
  public void clearMetadataCacheOnStartup() {
    //Cache cache = cacheManager.getCache("metadataFilters");
    Cache cache = cacheManager.getCache(CacheNames.METADATA_FILTERS_VERSION);

    if (cache != null) {
      cache.clear();
      log.info("Clearing METADATA_FILTERS_VERSION cache on startup");
    }
  }
}
