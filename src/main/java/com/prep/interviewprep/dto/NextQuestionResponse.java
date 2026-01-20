package com.prep.interviewprep.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NextQuestionResponse {

  private boolean completed;
  private QuestionResponse question;
  private int batchRemaining;
  private long sessionRemaining;
}
