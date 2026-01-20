package com.prep.interviewprep.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SessionResponse {

  private String sessionId;
  private List<QuestionResponse> questions;
  private long remainingCount;

}
