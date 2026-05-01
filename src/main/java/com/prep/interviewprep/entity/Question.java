package com.prep.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "questions",
    indexes = {
        @Index(name = "idx_question_category", columnList = "category"),
        @Index(name = "idx_question_sub_category", columnList = "subCategory"),
        @Index(name = "idx_question_difficulty", columnList = "difficulty"),
        @Index(name = "idx_question_deleted", columnList = "deleted")
    }
)
@Where(clause = "deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_seq")
    @SequenceGenerator(
        name = "question_seq",
        sequenceName = "question_seq",
        allocationSize = 1
    )
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Category category;

    @Column(nullable = false, length = 100)
    private String subCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;
    @Column(length = 500)
    private String link;
    @Column(nullable = false)
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String shortAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String codeSnippet;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
