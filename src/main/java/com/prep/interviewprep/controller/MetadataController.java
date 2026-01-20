package com.prep.interviewprep.controller;

import com.prep.interviewprep.dto.FiltersResponse;
import com.prep.interviewprep.service.MetadataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
public class MetadataController {

  private final MetadataService metadataService;


  @GetMapping("/filters")
  public FiltersResponse getFilters() {
    return metadataService.getFilters();
  }
}
