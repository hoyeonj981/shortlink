package me.hoyeon.shortlink.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExpirationTimeTest {

  @DisplayName("만료기간이 90일보다 작다면 객체를 생성한다")
  @Test
  void createObjectIfExpirationDaysIsUnder90() {
    var currentTime = Instant.parse("2024-01-01T10:00:00Z");
    var fixedClock = Clock.fixed(
        currentTime,
        ZoneId.systemDefault()
    );

    assertThatCode(() -> ExpirationTime.of(1, fixedClock))
        .doesNotThrowAnyException();
  }

  @DisplayName("만료기간이 현재 시간보다 이후라면 객체를 생성한다")
  @Test
  void createObjectIfExpirationTimeIsAfterCurrentTime() {
    var currentTime = Instant.parse("2024-01-01T10:00:00Z");
    var plusOneSec = currentTime.plusSeconds(1);
    var fixedClock = Clock.fixed(
        currentTime,
        ZoneId.systemDefault()
    );

    assertThatCode(() ->ExpirationTime.from(plusOneSec, fixedClock))
        .doesNotThrowAnyException();
  }

  @DisplayName("만료기간이 90일을 넘을 경우 예외가 발생한다")
  @Test
  void throwExceptionIfExpirationDaysIsOver90() {
    assertThatThrownBy(() -> ExpirationTime.of(91, Clock.systemDefaultZone()))
        .isInstanceOf(InvalidExpirationTimeException.class);
  }

  @DisplayName("만료시간이 현재 시간보다 이전일 경우 예외가 발생한다")
  @Test
  void throwExceptionIfExpirationTimeIsBeforeCurrentTime() {
    var currentTime = Instant.parse("2024-01-01T10:00:00Z");
    var pastTime = currentTime.minusSeconds(1);
    var fixedClock = Clock.fixed(
        currentTime,
        ZoneId.systemDefault()
    );

    assertThatThrownBy(() -> ExpirationTime.from(pastTime, fixedClock))
        .isInstanceOf(InvalidExpirationTimeException.class);
  }

  @DisplayName("만료일은 음수나 0으로 설정하면 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(ints = {-1, 0})
  void throwExceptionIfExpirationDaysIsNegativeOrZero(int givenDays) {
    assertThatThrownBy(() -> ExpirationTime.of(givenDays, Clock.systemDefaultZone()))
        .isInstanceOf(InvalidExpirationTimeException.class);
  }

  @DisplayName("만료시간이 현재 시간보다 이전이라면 true을 반환한다")
  @Test
  void returnTrueIfExpirationTimeIsBeforeCurrentTime() {
    var pastTime = Instant.parse("2024-01-01T10:00:00Z");
    var fixedClock = Clock.fixed(
        pastTime,
        ZoneId.systemDefault()
    );

    var expirationTime = ExpirationTime.from(pastTime, fixedClock);

    assertThat(expirationTime.isExpired()).isTrue();
  }

  @DisplayName("만료시간이 현재 시간보다 이후라면 false를 반환한다")
  @Test
  void returnFalseIfExpirationTimeIsAfterCurrentTime() {
    var pastTime = Instant.parse("2024-01-01T10:00:00Z");
    var currentTime = pastTime.plusSeconds(1);
    var fixedClock = Clock.fixed(
        pastTime,
        ZoneId.systemDefault()
    );

    var expirationTime = ExpirationTime.from(currentTime, fixedClock);

    assertThat(expirationTime.isExpired()).isFalse();
  }
}
