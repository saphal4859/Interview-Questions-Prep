package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.QuestionCreateRequest;
import com.prep.interviewprep.dto.QuestionResponse;
import com.prep.interviewprep.dto.QuestionSearchRequest;
import com.prep.interviewprep.dto.QuestionSearchResponse;

public interface QuestionService {
    QuestionResponse createQuestion(QuestionCreateRequest request);
    QuestionSearchResponse search (QuestionSearchRequest request);
}