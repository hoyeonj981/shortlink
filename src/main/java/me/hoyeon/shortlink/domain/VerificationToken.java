package me.hoyeon.shortlink.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import me.hoyeon.shortlink.infrastructure.DurationConverter;

@Embeddable
@AttributeOverrides({
    @AttributeOverride(
        name = "token",
        column = @Column(table = "verification_token", name = "token", nullable = false)),
    @AttributeOverride(
        name = "createdAt",
        column = @Column(table = "verification_token", name = "created_at", nullable = false)),
    @AttributeOverride(
        name = "duration",
        column = @Column(table = "verification_token", name = "duration_min", nullable = false)
    )
})
public class VerificationToken {

  private String token;

  private Instant createdAt;

  @Convert(converter = DurationConverter.class)
  private Duration validDuration;

  @Transient
  private Clock clock;

  public static VerificationToken create(String token, long expirationMinutes, Clock clock) {
    var createdAt = Instant.now(clock);
    validateDuration(expirationMinutes);
    var validDuration = Duration.ofMinutes(expirationMinutes);
    return new VerificationToken(token, createdAt, validDuration, clock);
  }

  protected VerificationToken() {}

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

  public void verify(String token) {
    matches(token);
    validateExpiration();
  }

  private void matches(String token) {
    if (!this.token.equals(token)) {
      throw new TokenMismatchException(token);
    }
  }

  private void validateExpiration() {
    if (isExpired()) {
      throw new TokenExpiredException();
    }
  }

  private boolean isExpired() {
    var now = Instant.now(clock);
    var between = Duration.between(createdAt, now);
    return between.compareTo(validDuration) > 0;
  }

  public String getTokenValue() {
    return this.token;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VerificationToken that = (VerificationToken) o;
    return Objects.equals(token, that.token) && Objects.equals(validDuration,
        that.validDuration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, validDuration);
  }
}
