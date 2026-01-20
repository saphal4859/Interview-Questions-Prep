package com.prep.interviewprep.dto;

import com.prep.interviewprep.entity.Category;
import com.prep.interviewprep.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionCreateRequest {

    @NotNull
    private Category category;

    @NotBlank
    private String subCategory;

    @NotNull
    private Difficulty difficulty;

    @NotBlank
    private String questionText;

    @NotBlank
    private String shortAnswer;

    private String explanation;
    private String codeSnippet;
}