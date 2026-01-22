package com.prep.interviewprep.service;

import com.prep.interviewprep.dto.FiltersResponse;
import com.prep.interviewprep.entity.Difficulty;
import com.prep.interviewprep.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetadataService {

  private final QuestionRepository questionRepository;
  @Cacheable(value = "metadataFilters")
  public FiltersResponse getFilters() {

    List<String> categories = new ArrayList<>(
        getCategories().stream()
            .sorted()
            .toList()
    );

    return new FiltersResponse(
        categories,
        getSubCategoriesGrouped(categories),
        new ArrayList<>(getDifficulties())
    );
  }

  /** Categories from DB */
  public List<String> getCategories() {
    return questionRepository.findDistinctCategories();
  }

  /** Subcategories grouped by category */
  public Map<String, List<String>> getSubCategoriesGrouped(List<String> categories) {

    Map<String, List<String>> map = new HashMap<>();

    for (String category : categories) {

      List<String> subs = new ArrayList<>(
          questionRepository
              .findDistinctSubCategories(Collections.singletonList(category))
              .stream()
              .map(String::toUpperCase)
              .sorted()
              .toList()
      );

      map.put(category, subs);
    }
    return map;
  }

  /** Difficulty from enum */
  public List<String> getDifficulties() {
    return new ArrayList<>(List.of(
        Difficulty.EASY.name(),
        Difficulty.MEDIUM.name(),
        Difficulty.HARD.name()
    ));
  }
}
