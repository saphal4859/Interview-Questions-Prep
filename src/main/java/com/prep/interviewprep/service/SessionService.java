package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.*;
import com.prep.interviewprep.entity.Question;
import com.prep.interviewprep.repository.QuestionRepository;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
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

    if (questionIds.isEmpty()) {
      throw new IllegalStateException("No questions found for selected filters");
    }

    // 2. Create session
    String sessionId = UUID.randomUUID().toString();

    // 3. Initialize Redis session (store all IDs)
    redisService.initializeSession(sessionId, questionIds);

    // 4. Fetch first batch
    List<Long> batchIds = redisService.popNextBatch(sessionId);
    List<QuestionResponse> questions = fetchQuestions(batchIds);

    // 5. Remaining count
    long remaining = redisService.remainingCount(sessionId);

    return SessionResponse.builder()
        .sessionId(sessionId)
        .questions(questions)
        .remainingCount(remaining)
        .build();
  }

  public NextQuestionResponse nextBatch(String sessionId) {

    // 1. Pop next batch from Redis
    List<Long> batchIds = redisService.popNextBatch(sessionId);

    // 2. If nothing left → session completed
    if (batchIds.isEmpty()) {
      return NextQuestionResponse.builder()
          .completed(true)
          .question(null)
          .batchRemaining(0)
          .sessionRemaining(0)
          .build();
    }

    // 3. Fetch questions preserving order
    Map<Long, Question> questionMap =
        questionRepository.findAllById(batchIds).stream()
            .collect(Collectors.toMap(Question::getId, q -> q));

    List<Question> batch = batchIds.stream()
        .map(questionMap::get)
        .filter(Objects::nonNull)
        .toList();

    // 4. Take FIRST question only
    Question current = batch.get(0);

    // 5. Remaining counts
    int batchRemaining = batch.size() - 1;
    long sessionRemaining = redisService.remainingCount(sessionId);

    return NextQuestionResponse.builder()
        .completed(false)
        .question(toResponse(current))
        .batchRemaining(batchRemaining)
        .sessionRemaining(sessionRemaining)
        .build();
  }


  /* ---------------- helpers ---------------- */

  private List<QuestionResponse> fetchQuestions(List<Long> ids) {

    if (ids.isEmpty()) {
      return List.of();
    }

    // Preserve order
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

