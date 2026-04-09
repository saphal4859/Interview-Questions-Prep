package com.prep.interviewprep.controller;

import com.prep.interviewprep.dto.QuestionCreateRequest;
import com.prep.interviewprep.dto.QuestionResponse;
import com.prep.interviewprep.dto.QuestionSearchRequest;
import com.prep.interviewprep.dto.QuestionSearchResponse;
import com.prep.interviewprep.dto.QuestionUpdateRequest;
import com.prep.interviewprep.service.QuestionService;
import jakarta.validation.Valid;
import java.util.List;
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
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<QuestionResponse> addQuestions(
        @Valid @RequestBody List<QuestionCreateRequest> requests
    ) {
        return questionService.createQuestions(requests);
    }
    @PostMapping("/search")
    public ResponseEntity<QuestionSearchResponse> search(
        @RequestBody QuestionSearchRequest request
    ) {
        return ResponseEntity.ok(questionService.search(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
        @PathVariable Long id,
        @Valid @RequestBody QuestionUpdateRequest request
    ) {
        return ResponseEntity.ok(questionService.updateQuestion(id, request));
    }
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadQuestions(
        @RequestBody QuestionSearchRequest request
    ) {
        byte[] file = questionService.downloadQuestions(request);

        String filename = buildFileName(request);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + filename)
            .header("Content-Type", "text/plain")
            .body(file);
    }
    private String buildFileName(QuestionSearchRequest request) {

        String category = (request.getCategories() != null && !request.getCategories().isEmpty())
            ? String.join("-", request.getCategories().stream().map(Enum::name).toList())
            : "ALL";

        String subCategory = (request.getSubCategories() != null && !request.getSubCategories().isEmpty())
            ? String.join("-", request.getSubCategories())
            : "ALL";

        String difficulty = (request.getDifficulties() != null && !request.getDifficulties().isEmpty())
            ? String.join("-", request.getDifficulties().stream().map(Enum::name).toList())
            : "ALL";

        return String.format("questions_%s_%s_%s.txt", category, subCategory, difficulty);
    }

}