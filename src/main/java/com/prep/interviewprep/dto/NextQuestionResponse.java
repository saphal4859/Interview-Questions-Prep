package com.prep.interviewprep.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NextQuestionResponse {

  private boolean completed;
  private List<QuestionResponse> questions;
  private long sessionRemaining;
}