package com.prep.interviewprep.controller;

import com.prep.interviewprep.dto.NextQuestionResponse;
import com.prep.interviewprep.dto.QuestionResponse;
import com.prep.interviewprep.dto.SessionNextRequest;
import com.prep.interviewprep.dto.SessionRequest;
import com.prep.interviewprep.dto.SessionResponse;
import com.prep.interviewprep.service.RedisQuestionSessionService;
import com.prep.interviewprep.service.SessionService;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

  private final SessionService sessionService;

  public SessionController(SessionService  sessionService) {
    this.sessionService = sessionService;
  }

  @PostMapping("/start")
  public SessionResponse startSession(@RequestBody SessionRequest request) {
    return sessionService.startSession(request);
  }
  @PostMapping("/next")
  public NextQuestionResponse next(@RequestBody SessionNextRequest req) {
    return sessionService.next(req.getSessionId());
  }
}