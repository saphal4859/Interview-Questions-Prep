package com.prep.interviewprep.controller;

import com.prep.interviewprep.dto.QuestionCreateRequest;
import com.prep.interviewprep.dto.QuestionResponse;
import com.prep.interviewprep.dto.QuestionSearchRequest;
import com.prep.interviewprep.dto.QuestionSearchResponse;
import com.prep.interviewprep.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse addQuestion(@Valid @RequestBody QuestionCreateRequest request) {
        return questionService.createQuestion(request);
    }
    @PostMapping("/search")
    public ResponseEntity<QuestionSearchResponse> search(
        @RequestBody QuestionSearchRequest request
    ) {
        return ResponseEntity.ok(questionService.search(request));
    }
}