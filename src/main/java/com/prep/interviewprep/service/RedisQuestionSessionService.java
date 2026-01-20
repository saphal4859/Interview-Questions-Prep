package com.prep.interviewprep.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
   * If empty → do nothing (VALID CASE).
   */
  public void initializeSession(String sessionId, List<Long> questionIds) {

    if (questionIds == null || questionIds.isEmpty()) {
      return; // ✅ graceful no-op
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
   * Pops next batch of IDs.
   * Uses SPOP → NO REPEATS guaranteed.
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

  public long remainingCount(String sessionId) {
    Long size = redisTemplate.opsForSet().size(remainingIdsKey(sessionId));
    return size == null ? 0 : size;
  }

  public void clearSession(String sessionId) {
    redisTemplate.delete(remainingIdsKey(sessionId));
  }
}
