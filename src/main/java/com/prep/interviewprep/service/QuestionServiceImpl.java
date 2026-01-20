package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.QuestionCreateRequest;
import com.prep.interviewprep.dto.QuestionResponse;
import com.prep.interviewprep.dto.QuestionSearchRequest;
import com.prep.interviewprep.dto.QuestionSearchResponse;
import com.prep.interviewprep.entity.Question;
import com.prep.interviewprep.repository.QuestionRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public QuestionResponse createQuestion(QuestionCreateRequest request) {

        Question question = Question.builder()
                .category(request.getCategory())
                .subCategory(request.getSubCategory())
                .difficulty(request.getDifficulty())
                .questionText(request.getQuestionText())
                .shortAnswer(request.getShortAnswer())
                .explanation(request.getExplanation())
                .codeSnippet(request.getCodeSnippet())
                .build();

        Question saved = questionRepository.save(question);

        return QuestionResponse.builder()
                .id(saved.getId())
                .category(saved.getCategory())
                .subCategory(saved.getSubCategory())
                .difficulty(saved.getDifficulty())
                .questionText(saved.getQuestionText())
                .shortAnswer(saved.getShortAnswer())
                .explanation(saved.getExplanation())
                .codeSnippet(saved.getCodeSnippet())
                .build();
    }
    public QuestionSearchResponse search(QuestionSearchRequest request) {

        List<Long> ids = questionRepository.findQuestionIdsByFilters(
            emptyToNull(request.getCategories()),
            emptyToNull(request.getSubCategories()),
            emptyToNull(request.getDifficulties())
        );

        return new QuestionSearchResponse(ids.size(), ids);
    }

    private <T> Set<T> emptyToNull(Set<T> set) {
        return (set == null || set.isEmpty()) ? null : set;
    }
}