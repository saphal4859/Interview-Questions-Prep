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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @CacheEvict(value = "metadataFilters", allEntries = true)
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
    @CacheEvict(value = "metadataFilters", allEntries = true)
    @Override
    public List<QuestionResponse> createQuestions(List<QuestionCreateRequest> requests) {

        List<Question> questions = requests.stream()
            .map(request -> Question.builder()
                .category(request.getCategory())
                .subCategory(request.getSubCategory())
                .difficulty(request.getDifficulty())
                .questionText(request.getQuestionText())
                .shortAnswer(request.getShortAnswer())
                .explanation(request.getExplanation())
                .codeSnippet(request.getCodeSnippet())
                .build())
            .toList();

        List<Question> savedQuestions = questionRepository.saveAll(questions);

        return savedQuestions.stream()
            .map(saved -> QuestionResponse.builder()
                .id(saved.getId())
                .category(saved.getCategory())
                .subCategory(saved.getSubCategory())
                .difficulty(saved.getDifficulty())
                .questionText(saved.getQuestionText())
                .shortAnswer(saved.getShortAnswer())
                .explanation(saved.getExplanation())
                .codeSnippet(saved.getCodeSnippet())
                .build())
            .toList();
    }
    @Cacheable(
        value = "questionSearch",
        key = "T(java.util.Objects).hash(#request.categories, #request.subCategories, #request.difficulties)"
    )
    @Transactional(readOnly = true)
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