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
          ORDER BY q.id
    """)
  List<Long> findQuestionIdsByFilters(
      @Param("categories") Set<Category> categories,
      @Param("subCategories") Set<String> subCategories,
      @Param("difficulties") Set<Difficulty> difficulties
  );
  @Query("select distinct q.category from Question q order by q.category")
  List<String> findDistinctCategories();

  @Query("""
        select distinct q.subCategory
        from Question q
        where q.category in :categories
        order by q.subCategory
    """)
  List<String> findDistinctSubCategories(@Param("categories") List<String> categories);
  // ---- SUMMARY ----
  @Query("select count(distinct q.category) from Question q")
  long countDistinctCategories();

  @Query("select count(distinct q.subCategory) from Question q")
  long countDistinctSubCategories();

  @Query("select count(q) from Question q")
  long countTotalQuestions();

  // ---- CATEGORY LEVEL ----
  @Query("""
    select q.category, count(q)
    from Question q
    group by q.category
  """)
  List<Object[]> countQuestionsByCategory();

  // ---- DIFFICULTY SPLIT ----
  @Query("""
    select q.category, q.difficulty, count(q)
    from Question q
    group by q.category, q.difficulty
  """)
  List<Object[]> difficultySplitByCategory();

  // ---- SUB CATEGORY ----
  @Query("""
    select q.category, q.subCategory, count(q)
    from Question q
    group by q.category, q.subCategory
  """)
  List<Object[]> subCategoryCountByCategory();
}