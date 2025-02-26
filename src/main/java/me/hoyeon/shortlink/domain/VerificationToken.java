package me.hoyeon.shortlink.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class VerificationToken {

  private final String token;
  private final Instant createdAt;
  private final Duration validDuration;
  private final Clock clock;

  public static VerificationToken create(String token, long expirationMinutes, Clock clock) {
    var createdAt = Instant.now(clock);
    validateDuration(expirationMinutes);
    var validDuration = Duration.ofMinutes(expirationMinutes);
    return new VerificationToken(token, createdAt, validDuration, clock);
  }

  private VerificationToken(String token, Instant createdAt, Duration validDuration, Clock clock) {
    this.token = token;
    this.createdAt = createdAt;
    this.validDuration = validDuration;
    this.clock = clock;
  }

  private static void validateDuration(long expirationMinutes) {
    if (expirationMinutes <= 0) {
      throw new InvalidDurationTimeException(expirationMinutes + "m");
    }
  }

  public boolean isVerified(String token) {
    if (!this.token.equals(token)) {
      return false;
    }
    return !isExpired();
  }

  private boolean isExpired() {
    var now = Instant.now(clock);
    var between = Duration.between(createdAt, now);
    return between.compareTo(validDuration) > 0;
  }
}
