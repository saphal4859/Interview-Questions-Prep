package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.*;
import com.prep.interviewprep.entity.Question;
import com.prep.interviewprep.repository.QuestionRepository;
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
  private final RedisQuestionSessionService redisSessionService;
  private final QuestionRepository questionRepository;


  public SessionResponse startSession(SessionRequest request) {

    // 1. Reuse EXISTING search logic
    QuestionSearchRequest searchRequest = new QuestionSearchRequest();
    searchRequest.setCategories(request.getCategories());
    searchRequest.setSubCategories(request.getSubCategories());
    searchRequest.setDifficulties(request.getDifficulties());

    QuestionSearchResponse searchResponse =
        questionService.search(searchRequest);

    List<Long> eligibleIds = searchResponse.getQuestionIds();

    if (eligibleIds.isEmpty()) {
      throw new RuntimeException("No questions found for given filters");
    }

    // 2. Create session
    String sessionId = UUID.randomUUID().toString();

    // 3. Init Redis
    redisSessionService.initializeSession(sessionId, eligibleIds);

    // 4. Fetch first batch
    Set<Long> batchIds = redisSessionService.fetchNextBatch(sessionId);

// 5. Fetch remaining count from Redis
    long remainingCount = redisSessionService.getRemainingCount(sessionId);

// 6. Fetch full questions
    List<Question> questions = questionRepository.findAllById(batchIds);

// 7. Map
    List<QuestionResponse> responses = questions.stream()
        .map(q -> QuestionResponse.builder()
            .id(q.getId())
            .category(q.getCategory())
            .subCategory(q.getSubCategory())
            .difficulty(q.getDifficulty())
            .questionText(q.getQuestionText())
            .shortAnswer(q.getShortAnswer())
            .explanation(q.getExplanation())
            .codeSnippet(q.getCodeSnippet())
            .build())
        .toList();

// 8. Return response
    SessionResponse response = new SessionResponse();
    response.setSessionId(sessionId);
    response.setQuestions(responses);
    response.setRemainingCount(remainingCount);

    return response;
  }
  public List<QuestionResponse> next(SessionNextRequest request) {

    String sessionId = request.getSessionId();

    // 1. Pop next batch of IDs from Redis
    Set<Long> batchIds = redisSessionService.fetchNextBatch(sessionId);

    if (batchIds.isEmpty()) {
      return List.of(); // frontend knows session ended
    }

    // 2. Fetch questions from DB
    List<Question> questions = questionRepository.findAllById(batchIds);

    // 3. Map to response
    return questions.stream()
        .map(q -> QuestionResponse.builder()
            .id(q.getId())
            .category(q.getCategory())
            .subCategory(q.getSubCategory())
            .difficulty(q.getDifficulty())
            .questionText(q.getQuestionText())
            .shortAnswer(q.getShortAnswer())
            .explanation(q.getExplanation())
            .codeSnippet(q.getCodeSnippet())
            .build())
        .collect(Collectors.toList());
  }
}
