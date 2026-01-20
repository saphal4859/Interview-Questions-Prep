package com.prep.interviewprep.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class Health {
  @GetMapping("/health-check")
  public String health() {
    return "OK";
  }

}
