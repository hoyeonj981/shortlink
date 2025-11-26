package me.hoyeon.shortlink.domain;

public class BehaviorRisk {

  private final int createdCountFromSameIpLastHour;

  public BehaviorRisk(int createdCountFromSameIpLastHour) {
    this.createdCountFromSameIpLastHour = createdCountFromSameIpLastHour;
  }
}
