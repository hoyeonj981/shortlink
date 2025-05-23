package me.hoyeon.shortlink.unit.domain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class MutableClock extends Clock {

  public MutableClock(Instant instant) {
    this.instant = instant;
  }

  Instant instant;

  @Override
  public ZoneId getZone() {
    return ZoneId.systemDefault();
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return null;
  }

  @Override
  public Instant instant() {
    return this.instant;
  }

  public void setInstant(Instant instant) {
    this.instant = instant;
  }
}
