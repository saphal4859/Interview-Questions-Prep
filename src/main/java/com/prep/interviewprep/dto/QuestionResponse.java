package com.prep.interviewprep.dto;

import com.prep.interviewprep.entity.Category;
import com.prep.interviewprep.entity.Difficulty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private Category category;
    private String subCategory;
    private Difficulty difficulty;
    private String questionText;
    private String shortAnswer;
    private String explanation;
    private String codeSnippet;
}