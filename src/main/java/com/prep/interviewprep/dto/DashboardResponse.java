package com.prep.interviewprep.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardResponse {
  private Summary summary;
  private List<CategoryStats> categories;

  @Getter @Setter
  public static class Summary {
    private long totalCategories;
    private long totalSubCategories;
    private long totalQuestions;
  }

  @Getter @Setter
  public static class CategoryStats {
    private String category;
    private long totalQuestions;
    private Map<String, Long> difficultySplit;
    private List<SubCategoryStats> subCategories;
  }

  @Getter @Setter
  public static class SubCategoryStats {
    private String name;
    private long questionCount;
  }
}
