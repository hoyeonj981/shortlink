package me.hoyeon.shortlink.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class ExpirationTime {

  private static final int EXPIRATION_DAYS = 90;
  private static final Duration MAX_DURATION = Duration.ofDays(EXPIRATION_DAYS);

  private final Instant expiration;
  private final Clock clock;

  public static ExpirationTime fromNowTo(int days, Clock clock) {
    validateDays(days);
    var now = Instant.now(clock);
    var expirationTime = now.plus(days, ChronoUnit.DAYS);
    return new ExpirationTime(expirationTime, clock);
  }

  public static ExpirationTime from(Instant instant, Clock clock) {
    return new ExpirationTime(instant, clock);
  }

  private static void validateDays(int days) {
    if (days <= 0) {
      throw new InvalidExpirationTimeException();
    }
  }

  private ExpirationTime(Instant expiration, Clock clock) {
    validateExpirationTime(expiration, clock);
    this.expiration = expiration;
    this.clock = clock;
  }

  private void validateExpirationTime(Instant expirationTime, Clock clock) {
    validateIsBeforeTheCurrent(expirationTime, clock);
    validateMaxDuration(expirationTime, clock);
  }

  private void validateMaxDuration(Instant expirationTime, Clock clock) {
    var now = Instant.now(clock);
    var between = Duration.between(now, expirationTime);
    if (between.compareTo(MAX_DURATION) > 0) {
      throw new InvalidExpirationTimeException();
    }
  }

  private void validateIsBeforeTheCurrent(Instant expirationTime, Clock clock) {
    var now = Instant.now(clock);
    if (expirationTime.isBefore(now)) {
      throw new InvalidExpirationTimeException();
    }
  }

  public boolean isExpired() {
    return Instant.now(this.clock).isAfter(this.expiration);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpirationTime that = (ExpirationTime) o;
    return Objects.equals(expiration, that.expiration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expiration);
  }
}
