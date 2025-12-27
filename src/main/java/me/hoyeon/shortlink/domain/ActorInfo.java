package me.hoyeon.shortlink.domain;

import java.time.Instant;

public class ActorInfo {

  private final String clientIp;
  private final String userId;
  private final Instant requestedAt;

  public ActorInfo(String clientIp, String userId, Instant requestedAt) {
    this.clientIp = clientIp;
    this.userId = userId;
    this.requestedAt = requestedAt;
  }
}
