package com.prep.interviewprep.dto;

import com.prep.interviewprep.entity.Category;
import com.prep.interviewprep.entity.Difficulty;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SessionRequest {

  private Set<Category> categories;
  private Set<String> subCategories;
  private Set<Difficulty> difficulties;
}
