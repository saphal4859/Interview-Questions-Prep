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

  private final RedisTemplate<String, Long> redisTemplate;

  public RedisQuestionSessionService(RedisTemplate<String, Long> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /* ===================== KEYS ===================== */

  private String remainingIdsKey(String sessionId) {
    return "session:" + sessionId + ":remainingQuestionIds";
  }

  /* ===================== INIT ===================== */

  /**
   * Initializes Redis LIST with all eligible question IDs.
   * Order of IDs is preserved.
   */
  public void initializeSession(String sessionId, List<Long> questionIds) {

    if (questionIds == null || questionIds.isEmpty()) {
      return; // graceful no-op
    }

    String key = remainingIdsKey(sessionId);

    // Safety: clear any existing session data
    redisTemplate.delete(key);

    // Push all IDs in order
    redisTemplate.opsForList().rightPushAll(key, questionIds);

    // Set TTL for session
    redisTemplate.expire(key, SESSION_TTL);
  }

  /* ===================== FETCH ===================== */

  /**
   * Pops the next batch of question IDs in order.
   * Uses LIST → deterministic order guaranteed.
   */
  public List<Long> popNextBatch(String sessionId) {

    String key = remainingIdsKey(sessionId);
    List<Long> result = new ArrayList<>(BATCH_SIZE);

    for (int i = 0; i < BATCH_SIZE; i++) {
      Long value = redisTemplate.opsForList().leftPop(key);
      if (value == null) {
        break;
      }
      result.add(value);
    }

    return result;
  }

  /* ===================== UTILS ===================== */

  /**
   * Returns remaining question count for the session.
   */
  public long remainingCount(String sessionId) {
    Long size = redisTemplate.opsForList().size(remainingIdsKey(sessionId));
    return size == null ? 0 : size;
  }

  /**
   * Clears session explicitly (optional cleanup).
   */
  public void clearSession(String sessionId) {
    redisTemplate.delete(remainingIdsKey(sessionId));
  }
}
