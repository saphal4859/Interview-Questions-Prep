package com.prep.interviewprep.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltersResponse {

  private List<String> categories;
  private Map<String, List<String>> subCategories;
  private List<String> difficulties;
}
