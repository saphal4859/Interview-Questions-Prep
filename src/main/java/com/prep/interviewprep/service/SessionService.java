package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.*;
import com.prep.interviewprep.entity.Question;
import com.prep.interviewprep.repository.QuestionRepository;
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

  private final QuestionService questionService;
  private final RedisQuestionSessionService redisService;
  private final QuestionRepository questionRepository;

  public SessionResponse startSession(SessionRequest request) {

    QuestionSearchResponse search =
        questionService.search(toSearchRequest(request));

    if (search.getQuestionIds().isEmpty()) {
      throw new IllegalStateException("No questions found");
    }

    String sessionId = UUID.randomUUID().toString();
    redisService.initializeSession(sessionId, search.getQuestionIds());

    List<QuestionResponse> firstBatch = fetchBatch(sessionId);

    return SessionResponse.builder()
        .sessionId(sessionId)
        .questions(firstBatch)
        .remainingCount(redisService.remainingCount(sessionId))
        .build();
  }

  public NextQuestionResponse next(String sessionId) {

    List<Question> batch = fetchBatchEntities(sessionId);

    if (batch.isEmpty()) {
      return NextQuestionResponse.builder()
          .completed(true)
          .batchRemaining(0)
          .sessionRemaining(0)
          .build();
    }

    Question q = batch.get(0);

    return NextQuestionResponse.builder()
        .completed(false)
        .question(toResponse(q))
        .batchRemaining(batch.size() - 1)
        .sessionRemaining(redisService.remainingCount(sessionId))
        .build();
  }


  /* ---------------- helpers ---------------- */

  private List<QuestionResponse> fetchBatch(String sessionId) {
    return fetchBatchEntities(sessionId).stream()
        .map(this::toResponse)
        .toList();
  }

  private List<Question> fetchBatchEntities(String sessionId) {
    List<Long> ids = redisService.popNextBatch(sessionId);

    if (ids.isEmpty()) return List.of();

    // preserve order explicitly
    Map<Long, Question> map =
        questionRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(Question::getId, q -> q));

    return ids.stream()
        .map(map::get)
        .filter(Objects::nonNull)
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


  private QuestionSearchRequest toSearchRequest(SessionRequest r) {
    QuestionSearchRequest q = new QuestionSearchRequest();
    q.setCategories(r.getCategories());
    q.setSubCategories(r.getSubCategories());
    q.setDifficulties(r.getDifficulties());
    return q;
  }
}
