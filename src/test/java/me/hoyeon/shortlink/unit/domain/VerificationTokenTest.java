package me.hoyeon.shortlink.unit.domain;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import me.hoyeon.shortlink.domain.InvalidDurationTimeException;
import me.hoyeon.shortlink.domain.TokenExpiredException;
import me.hoyeon.shortlink.domain.TokenMismatchException;
import me.hoyeon.shortlink.domain.VerificationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerificationTokenTest {

  private static final Instant STANDARD_TIME = Instant.parse("2024-02-02T10:00:00Z");
  private static final Clock STANDARD_CLOCK = Clock.fixed(STANDARD_TIME, systemDefault());

  @DisplayName("주어진 토큰 값과 만료시간이 유효하다면 검증에 성공한다")
  @Test
  void validateSuccessfullyIfGivenTokenMatchesAndTimeIsNotExpired() {
    var givenToken = "abcde";
    var expirationMinutes = 5;

    var token = VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK);

    assertThatCode(() -> token.verify(givenToken)).doesNotThrowAnyException();
  }

  @DisplayName("주어진 토큰 값과 다르다면 예외가 발생한다")
  @Test
  void throwExceptionIfGivenTokenIsNotCorrect() {
    var givenToken = "abcde";
    var wrongToken = "1234";
    var expirationMinutes = 5;

    var token = VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK);

    assertThat(givenToken).isNotEqualTo(wrongToken);
    assertThatThrownBy(() -> token.verify(wrongToken))
        .isInstanceOf(TokenMismatchException.class);
  }

  @DisplayName("토큰 시간이 만료되었다면 예외가 발생한다")
  @Test
  void throwExceptionIfTimeIsExpired() {
    var givenToken = "abcde";
    var expirationMinutes = 5;
    var mutableClock = new MutableClock(STANDARD_TIME);

    var token = VerificationToken.create(givenToken, expirationMinutes, mutableClock);
    mutableClock.setInstant(STANDARD_TIME.plus(expirationMinutes + 1, ChronoUnit.MINUTES));

    assertThatThrownBy(() -> token.verify(givenToken))
        .isInstanceOf(TokenExpiredException.class);
  }

  @DisplayName("만료 시간은 음수로 설정할 수 없다")
  @Test
  void expirationTimeShouldNotBeNegative() {
    var givenToken = "abcde";
    var expirationMinutes = -1;

    assertThatThrownBy(() ->
        VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK))
        .isInstanceOf(InvalidDurationTimeException.class);
  }

  @DisplayName("만료 시간은 0으로 설정할 수 없다")
  @Test
  void expirationTimeShouldNotBeZero() {
    var givenToken = "abcde";
    var expirationMinutes = 0;

    assertThatThrownBy(() ->
        VerificationToken.create(givenToken, expirationMinutes, STANDARD_CLOCK))
        .isInstanceOf(InvalidDurationTimeException.class);
  }

  @DisplayName("토큰 값과 만료 시간이 동일하면 같은 객체이다.")
  @Test
  void sameObjectIfTokenValueAndExpirationTimeIsSame() {
    var givenToken1 = "abcde";
    var expirationMinutes1 = 5;
    var givenToken2 = givenToken1;
    var expirationMinutes2 = expirationMinutes1;

    var token1 = VerificationToken.create(givenToken1, expirationMinutes1,
        STANDARD_CLOCK);
    var token2 = VerificationToken.create(givenToken2, expirationMinutes2,
        STANDARD_CLOCK);

    assertThat(token1).isEqualTo(token2);
  }

  @DisplayName("토큰 값이 같아도 만료 시간이 다르다면 다른 객체이다")
  @Test
  void differentObjectIfExpirationTimeIsNotSame() {
    var givenToken1 = "abcde";
    var expirationMinutes1 = 5;
    var givenToken2 = givenToken1;
    var expirationMinutes2 = 10;

    var token1 = VerificationToken.create(givenToken1, expirationMinutes1,
        STANDARD_CLOCK);
    var token2 = VerificationToken.create(givenToken2, expirationMinutes2,
        STANDARD_CLOCK);

    assertThat(expirationMinutes1).isNotEqualTo(expirationMinutes2);
    assertThat(token1).isNotEqualTo(token2);
  }

  @DisplayName("토큰 값과 만료 시간이 다르다면 다른 객체이다")
  @Test
  void differenetObjectIfTokenValueAndExpirationTimeIsNotSame() {
    var givenToken1 = "abcde";
    var expirationMinutes1 = 5;
    var givenToken2 = "ABCDE";
    var expirationMinutes2 = 10;

    var token1 = VerificationToken.create(givenToken1, expirationMinutes1,
        STANDARD_CLOCK);
    var token2 = VerificationToken.create(givenToken2, expirationMinutes2,
        STANDARD_CLOCK);

    assertThat(givenToken1).isNotEqualTo(givenToken2);
    assertThat(expirationMinutes1).isNotEqualTo(expirationMinutes2);
    assertThat(token1).isNotEqualTo(token2);
  }
}
