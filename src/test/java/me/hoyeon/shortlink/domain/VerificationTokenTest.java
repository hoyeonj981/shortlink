package me.hoyeon.shortlink.domain;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationTokenTest {

  private static final Instant STANDARD_TIME = Instant.parse("2024-02-02T10:00:00Z");
  private static final Clock STANDARD_CLOCK = Clock.fixed(STANDARD_TIME, systemDefault());

  @DisplayName("주어진 토큰 값과 다르다면 false를 반환한다")
  @Test
  void returnFalseIfGivenTokenIsNotCorrect() {
    var givenToken = "abcde";
    var wrongToken = "1234";
    var expirationMinutes = 5;

    var token = VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK);

    assertThat(givenToken).isNotEqualTo(wrongToken);
    assertThat(token.isVerified(wrongToken)).isFalse();
  }

  @DisplayName("토큰값이 일치해도 시간이 만료되었다면 false를 반환한다")
  @Test
  void returnFalseIfTimeIsExpired() {
    var givenToken = "abcde";
    var expirationMinutes = 5;
    var mutableClock = new MutableClock(STANDARD_TIME);

    var token = VerificationToken.create(givenToken, expirationMinutes, mutableClock);
    mutableClock.setInstant(STANDARD_TIME.plus(expirationMinutes + 1, ChronoUnit.MINUTES));

    assertThat(token.isVerified(givenToken)).isFalse();
  }

  @DisplayName("주어진 토큰 값과 일치하고 시간이 만료가 안 됐다면 true를 반환한다")
  @Test
  void returnTrueIfGivenTokenIsCorrectAndTimeIsNotExpired() {
    var givenToken = "abcde";
    var expirationMinutes = 5;
    var mutableClock = new MutableClock(STANDARD_TIME);

    var token = VerificationToken.create(givenToken, expirationMinutes, mutableClock);
    mutableClock.setInstant(STANDARD_TIME.plus(1, ChronoUnit.MINUTES));

    assertThat(token.isVerified(givenToken)).isTrue();
  }

  @DisplayName("만료 시간은 음수로 설정할 수 없다")
  @Test
  void expirationTimeShouldNotBeNegative() {
    var givenToken = "abcde";
    var expirationMinutes = -1;

    assertThatThrownBy(() -> VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK))
        .isInstanceOf(InvalidDurationTimeException.class);
  }

  @DisplayName("만료 시간은 0으로 설정할 수 없다")
  @Test
  void expirationTimeShouldNotBeZero() {
    var givenToken = "abcde";
    var expirationMinutes = 0;

    assertThatThrownBy(() -> VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK))
        .isInstanceOf(InvalidDurationTimeException.class);
  }
}
