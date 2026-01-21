package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.DashboardResponse;
import com.prep.interviewprep.repository.QuestionRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final QuestionRepository questionRepository;

  public DashboardResponse getOverview() {

    DashboardResponse response = new DashboardResponse();

    // ---------------- SUMMARY ----------------
    DashboardResponse.Summary summary = new DashboardResponse.Summary();
    summary.setTotalCategories(questionRepository.countDistinctCategories());
    summary.setTotalSubCategories(questionRepository.countDistinctSubCategories());
    summary.setTotalQuestions(questionRepository.countTotalQuestions());
    response.setSummary(summary);

    // ---------------- CATEGORY MAP ----------------
    Map<String, DashboardResponse.CategoryStats> categoryMap = new LinkedHashMap<>();

    // ---- total questions per category
    for (Object[] row : questionRepository.countQuestionsByCategory()) {
      String category = row[0].toString();
      long count = (long) row[1];

      DashboardResponse.CategoryStats cs = new DashboardResponse.CategoryStats();
      cs.setCategory(category);
      cs.setTotalQuestions(count);
      cs.setDifficultySplit(new HashMap<>());
      cs.setSubCategories(new ArrayList<>());

      categoryMap.put(category, cs);
    }

    // ---- difficulty split
    for (Object[] row : questionRepository.difficultySplitByCategory()) {
      String category = row[0].toString();
      String difficulty = row[1].toString();
      long count = (long) row[2];

      categoryMap
          .get(category)
          .getDifficultySplit()
          .put(difficulty, count);
    }

    // ---- sub categories
    for (Object[] row : questionRepository.subCategoryCountByCategory()) {
      String category = row[0].toString();
      String subCategory = row[1].toString();
      long count = (long) row[2];

      DashboardResponse.SubCategoryStats sc =
          new DashboardResponse.SubCategoryStats();
      sc.setName(subCategory);
      sc.setQuestionCount(count);

      categoryMap.get(category).getSubCategories().add(sc);
    }

    response.setCategories(new ArrayList<>(categoryMap.values()));
    return response;
  }
}
