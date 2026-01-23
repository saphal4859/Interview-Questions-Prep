package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.*;
import com.prep.interviewprep.entity.Question;
import com.prep.interviewprep.repository.QuestionRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

  private final RedisQuestionSessionService redisService;
  private final QuestionRepository questionRepository;

  public SessionResponse startSession(SessionRequest request) {

    // 1. Fetch all matching question IDs
    List<Long> questionIds = questionRepository.findQuestionIdsByFilters(
        request.getCategories(),
        request.getSubCategories(),
        request.getDifficulties()
    );

    int totalCount = questionIds.size();

    // ✅ Valid empty result
    if (totalCount == 0) {
      return SessionResponse.builder()
          .sessionId(null)
          .questions(List.of())
          .totalCount(0)
          .build();
    }

    // 2. Randomize once per session
    if (request.isShuffle()) {
      Collections.shuffle(questionIds);
    }


    // 3. Create session
    String sessionId = UUID.randomUUID().toString();

    // 4. Initialize Redis with ALL IDs
    redisService.initializeSession(sessionId, questionIds);

    // 5. Fetch first batch
    List<Long> batchIds = redisService.popNextBatch(sessionId);
    List<QuestionResponse> questions = fetchQuestions(batchIds);
    log.info("DB IDS: {}", questionIds);
    log.info("REDIS POP IDS: {}", batchIds);
    log.info("FINAL IDS: {}", questions.stream().map(QuestionResponse::getId).toList());

    return SessionResponse.builder()
        .sessionId(sessionId)
        .questions(questions)
        .totalCount(totalCount)
        .build();
  }

  public NextQuestionResponse nextBatch(String sessionId) {

    // Defensive: invalid session
    if (sessionId == null || sessionId.isBlank()) {
      return NextQuestionResponse.builder()
          .completed(true)
          .questions(List.of())
          .sessionRemaining(0)
          .build();
    }

    // 1. Pop next batch
    List<Long> batchIds = redisService.popNextBatch(sessionId);

    // 2. No more questions → completed
    if (batchIds.isEmpty()) {
      return NextQuestionResponse.builder()
          .completed(true)
          .questions(List.of())
          .sessionRemaining(0)
          .build();
    }

    // 3. Fetch questions preserving order
    List<QuestionResponse> questions = fetchQuestions(batchIds);
    long sessionRemaining = redisService.remainingCount(sessionId);

    return NextQuestionResponse.builder()
        .completed(false)
        .questions(questions)
        .sessionRemaining(sessionRemaining)
        .build();
  }

  /* ================= HELPERS ================= */

  private List<QuestionResponse> fetchQuestions(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    Map<Long, Question> questionMap =
        questionRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(Question::getId, q -> q));

    return ids.stream()
        .map(questionMap::get)
        .filter(Objects::nonNull)
        .map(this::toResponse)
        .toList();
  }

  private QuestionResponse toResponse(Question q) {
    return QuestionResponse.builder()
        .id(q.getId())
        .category(q.getCategory())
        .subCategory(q.getSubCategory())
        .difficulty(q.getDifficulty())
        .questionText(q.getQuestionText())
        .shortAnswer(q.getShortAnswer())
        .explanation(q.getExplanation())
        .codeSnippet(q.getCodeSnippet())
        .build();
  }
}
