package com.prep.interviewprep.controller;

import com.prep.interviewprep.dto.DashboardResponse;
import com.prep.interviewprep.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/overview")
  public DashboardResponse overview() {
    return dashboardService.getOverview();
  }
}
