package com.prep.interviewprep.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {

  private String sessionId;
  private List<QuestionResponse> questions;
  private long totalCount;
}
