package com.prep.interviewprep.repository;

import com.prep.interviewprep.entity.Category;
import com.prep.interviewprep.entity.Difficulty;
import com.prep.interviewprep.entity.Question;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {
  @Query("""
        SELECT q.id FROM Question q
        WHERE (:categories IS NULL OR q.category IN :categories)
          AND (:subCategories IS NULL OR q.subCategory IN :subCategories)
          AND (:difficulties IS NULL OR q.difficulty IN :difficulties)
    """)
  List<Long> findQuestionIdsByFilters(
      @Param("categories") Set<Category> categories,
      @Param("subCategories") Set<String> subCategories,
      @Param("difficulties") Set<Difficulty> difficulties
  );
}