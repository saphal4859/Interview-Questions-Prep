package com.prep.interviewprep.service;

import java.util.ArrayList;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisQuestionSessionService {

  private static final int BATCH_SIZE = 10;
  private static final Duration SESSION_TTL = Duration.ofHours(24);

  private final RedisTemplate<String, String> redisTemplate;

  public RedisQuestionSessionService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /* ===================== KEYS ===================== */

  private String remainingIdsKey(String sessionId) {
    return "session:" + sessionId + ":remainingQuestionIds";
  }

  /* ===================== INIT ===================== */

  /**
   * Initializes Redis SET with all eligible question IDs.
   * Redis stores ONLY IDs.
   * TTL is for cleanup only.
   */
  public void initializeSession(String sessionId, List<Long> questionIds) {
    if (questionIds == null || questionIds.isEmpty()) {
      throw new IllegalArgumentException("Cannot initialize session with empty question list");
    }

    String key = remainingIdsKey(sessionId);

    redisTemplate.opsForSet().add(
        key,
        questionIds.stream()
            .map(String::valueOf)
            .toArray(String[]::new)
    );

    redisTemplate.expire(key, SESSION_TTL);
  }

  /* ===================== FETCH ===================== */

  /**
   * Atomically pops the next batch of question IDs.
   * Uses Redis SPOP → guarantees NO REPEATS even under concurrency.
   */
  public List<Long> popNextBatch(String sessionId) {
    String key = remainingIdsKey(sessionId);

    List<Long> result = new ArrayList<>(BATCH_SIZE);

    for (int i = 0; i < BATCH_SIZE; i++) {
      String value = redisTemplate.opsForSet().pop(key);
      if (value == null) {
        break;
      }
      result.add(Long.valueOf(value));
    }

    return result;
  }


  /* ===================== UTILS ===================== */

  public long getRemainingCount(String sessionId) {
    Long size = redisTemplate.opsForSet().size(remainingIdsKey(sessionId));
    return size == null ? 0 : size;
  }

  public boolean hasRemainingQuestions(String sessionId) {
    return getRemainingCount(sessionId) > 0;
  }

  public void clearSession(String sessionId) {
    redisTemplate.delete(remainingIdsKey(sessionId));
  }
  public long remainingCount(String sessionId) {
    Long size = redisTemplate.opsForSet()
        .size(remainingIdsKey(sessionId));

    return size == null ? 0 : size;
  }

}
