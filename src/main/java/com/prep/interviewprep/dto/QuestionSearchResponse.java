package com.prep.interviewprep.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionSearchResponse {

  private long total;
  private List<Long> questionIds;

  public QuestionSearchResponse(long total, List<Long> questionIds) {
    this.total = total;
    this.questionIds = questionIds;
  }

  public long getTotal() {
    return total;
  }

  public List<Long> getQuestionIds() {
    return questionIds;
  }
}