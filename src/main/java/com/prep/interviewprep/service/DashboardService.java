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

    // category → subcategory map
    Map<String, DashboardResponse.CategoryStats> categoryMap = new LinkedHashMap<>();
    Map<String, Map<String, DashboardResponse.SubCategoryStats>> subCategoryMap = new HashMap<>();

    List<Object[]> rows = questionRepository.getFullDashboardData();

    for (Object[] row : rows) {
      String category = row[0].toString();
      String subCategory = row[1].toString();
      String difficulty = row[2].toString();
      long count = (long) row[3];

      // ---------------- CATEGORY ----------------
      DashboardResponse.CategoryStats cs =
          categoryMap.computeIfAbsent(category, k -> {
            DashboardResponse.CategoryStats c = new DashboardResponse.CategoryStats();
            c.setCategory(category);
            c.setSubCategories(new ArrayList<>());
            return c;
          });

      // ---------------- SUBCATEGORY MAP ----------------
      Map<String, DashboardResponse.SubCategoryStats> subMap =
          subCategoryMap.computeIfAbsent(category, k -> new HashMap<>());

      DashboardResponse.SubCategoryStats sc =
          subMap.computeIfAbsent(subCategory, k -> {
            DashboardResponse.SubCategoryStats s = new DashboardResponse.SubCategoryStats();
            s.setName(subCategory);
            return s;
          });

      // ---------------- APPLY COUNTS ----------------
      switch (difficulty) {
        case "EASY" -> {
          cs.setEasy(cs.getEasy() + count);
          sc.setEasy(sc.getEasy() + count);
        }
        case "MEDIUM" -> {
          cs.setMedium(cs.getMedium() + count);
          sc.setMedium(sc.getMedium() + count);
        }
        case "HARD" -> {
          cs.setHard(cs.getHard() + count);
          sc.setHard(sc.getHard() + count);
        }
      }

      // totals
      cs.setTotalQuestions(cs.getTotalQuestions() + count);
      sc.setQuestionCount(sc.getQuestionCount() + count);
    }

    // ---------------- CONVERT MAP → LIST ----------------
    for (String category : categoryMap.keySet()) {
      Map<String, DashboardResponse.SubCategoryStats> subMap = subCategoryMap.get(category);

      categoryMap.get(category)
          .setSubCategories(new ArrayList<>(subMap.values()));
    }

    // ---------------- FINAL RESPONSE ----------------
    response.setCategories(new ArrayList<>(categoryMap.values()));

    // ---------------- SUMMARY ----------------
    DashboardResponse.Summary summary = new DashboardResponse.Summary();

    summary.setTotalCategories(categoryMap.size());

    summary.setTotalSubCategories(
        subCategoryMap.values().stream()
            .mapToLong(Map::size)
            .sum());

    summary.setTotalQuestions(
        categoryMap.values().stream()
            .mapToLong(DashboardResponse.CategoryStats::getTotalQuestions)
            .sum());

    response.setSummary(summary);

    return response;
  }
}