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
        WHERE q.deleted = false
          AND (:categories IS NULL OR q.category IN :categories)
          AND (:subCategories IS NULL OR q.subCategory IN :subCategories)
          AND (:difficulties IS NULL OR q.difficulty IN :difficulties)
        ORDER BY q.id ASC
    """)
  List<Long> findQuestionIdsByFilters(
      @Param("categories") Set<Category> categories,
      @Param("subCategories") Set<String> subCategories,
      @Param("difficulties") Set<Difficulty> difficulties
  );

  @Query("""
        SELECT DISTINCT CAST(q.category AS string)
        FROM Question q
        WHERE q.deleted = false
        ORDER BY q.category
    """)
  List<String> findDistinctCategories();

  @Query("""
        SELECT DISTINCT q.subCategory
        FROM Question q
        WHERE q.deleted = false
          AND q.category IN :categories
        ORDER BY q.subCategory
    """)
  List<String> findDistinctSubCategories(
      @Param("categories") List<String> categories
  );

  @Query("""
        SELECT 
            q.category,
            q.subCategory,
            q.difficulty,
            COUNT(q)
        FROM Question q
        WHERE q.deleted = false
        GROUP BY q.category, q.subCategory, q.difficulty
        ORDER BY q.category, q.subCategory
    """)
  List<Object[]> getFullDashboardData();

  @Query("""
        SELECT q.questionText 
        FROM Question q
        WHERE q.deleted = false
          AND (:categories IS NULL OR q.category IN :categories)
          AND (:subCategories IS NULL OR q.subCategory IN :subCategories)
          AND (:difficulties IS NULL OR q.difficulty IN :difficulties)
        ORDER BY q.id ASC
    """)
  List<String> findQuestionTextsByFilters(
      @Param("categories") Set<Category> categories,
      @Param("subCategories") Set<String> subCategories,
      @Param("difficulties") Set<Difficulty> difficulties
  );
}