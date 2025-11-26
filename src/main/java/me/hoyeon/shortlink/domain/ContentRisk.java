package me.hoyeon.shortlink.domain;

import java.util.Set;

public class ContentRisk {

  private final Set<ContentRiskCategory> categories;
  private final double nsfwScore;

  public ContentRisk(Set<ContentRiskCategory> categories, double nsfwScore) {
    this.categories = categories;
    this.nsfwScore = nsfwScore;
  }
}
