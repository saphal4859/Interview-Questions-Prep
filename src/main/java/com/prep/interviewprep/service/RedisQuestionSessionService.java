package com.prep.interviewprep.service;


import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisQuestionSessionService {

  private static final int DEFAULT_BATCH_SIZE = 10;
  private static final Duration SESSION_TTL = Duration.ofHours(24);

  private final RedisTemplate<String, String> redisTemplate;

  public RedisQuestionSessionService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /* ===================== KEY ===================== */

  private String getSessionKey(String sessionId) {
    return "session:" + sessionId + ":remainingQuestionIds";
  }

  /* ===================== INIT ===================== */

  /**
   * Initialize Redis SET with all eligible question IDs.
   */
  public void initializeSession(String sessionId, List<Long> questionIds) {
    String key = getSessionKey(sessionId);

    Set<String> values =
        questionIds.stream()
            .map(String::valueOf)
            .collect(Collectors.toSet());

    redisTemplate.opsForSet().add(key, values.toArray(new String[0]));
    redisTemplate.expire(key, SESSION_TTL);
  }

  /* ===================== FETCH ===================== */

  /**
   * Fetch next random batch of question IDs.
   * Uses Redis SPOP → guarantees no repetition.
   */
  public Set<Long> fetchNextBatch(String sessionId) {
    String key = getSessionKey(sessionId);

    List<String> popped =
        redisTemplate.opsForSet().pop(key, DEFAULT_BATCH_SIZE);

    if (popped == null || popped.isEmpty()) {
      return Set.of();
    }

    return popped.stream()
        .map(Long::valueOf)
        .collect(Collectors.toSet());
  }


  /* ===================== UTILS ===================== */

  public boolean hasRemainingQuestions(String sessionId) {
    String key = getSessionKey(sessionId);
    Long size = redisTemplate.opsForSet().size(key);
    return size != null && size > 0;
  }

  public void clearSession(String sessionId) {
    redisTemplate.delete(getSessionKey(sessionId));
  }
  public long getRemainingCount(String sessionId) {
    String key = getSessionKey(sessionId);
    Long size = redisTemplate.opsForSet().size(key);
    return size != null ? size : 0;
  }

}
