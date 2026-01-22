package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.QuestionCreateRequest;
import com.prep.interviewprep.dto.QuestionResponse;
import com.prep.interviewprep.dto.QuestionSearchRequest;
import com.prep.interviewprep.dto.QuestionSearchResponse;
import com.prep.interviewprep.dto.QuestionUpdateRequest;
import java.util.List;

public interface QuestionService {
    QuestionResponse createQuestion(QuestionCreateRequest request);
    List<QuestionResponse> createQuestions(List<QuestionCreateRequest> request);

    QuestionSearchResponse search (QuestionSearchRequest request);
    QuestionResponse updateQuestion(Long id, QuestionUpdateRequest request);
}