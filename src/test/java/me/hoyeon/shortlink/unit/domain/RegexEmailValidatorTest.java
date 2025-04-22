package me.hoyeon.shortlink.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import me.hoyeon.shortlink.domain.RegexEmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RegexEmailValidatorTest {

  private EmailValidator emailValidator;

  @BeforeEach
  void setUp() {
    emailValidator = new RegexEmailValidator();
  }

  @DisplayName("유효한 이메일 형식이면 true을 반환한다")
  @ParameterizedTest
  @ValueSource(strings = {
      "user@example.com",
      "user.name@example.com",
      "user+tag@example.com",
      "user_name@example.com",
      "user-name@example.co.kr",
      "user123@example.com",
      "USER@EXAMPLE.COM",
      "user@subdomain.example.com",
      "user@123.45.67.89"
  })
  void shouldReturnTrueForValidEmails(String email) {
    var result = emailValidator.isValid(email);

    assertThat(result).isTrue();
  }

  @DisplayName("잘못된 이메일 형식이면 false를 반환한다")
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {
      "user",
      "user@",
      "@example.com",
      "user@exam ple.com",
      "user@exam\tple.com",
      "user@exam_ple.com",
      " ", "  ", "\t", "\n"
  })
  void shouldReturnFalseForInvalidEmails(String email) {
    var result = emailValidator.isValid(email);

    assertThat(result).isFalse();
  }

  @DisplayName("유효한 이메일은 예외를 발생시키지 않는다")
  @ParameterizedTest
  @ValueSource(strings = {
      "user@example.com",
      "user.name@example.co.kr",
      "user_name@example.com"
  })
  void shouldThrowExceptionForValidEmails(String email) {
    assertThatCode(() -> emailValidator.validate(email))
        .doesNotThrowAnyException();
  }

  @DisplayName("유효하지 않은 이메일은 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(strings = {
      "user",
      "user@",
      "@example.com"
  })
  void shouldThrowExceptionForInvalidEmails(String email) {
    assertThatThrownBy(() -> emailValidator.validate(email))
        .isInstanceOf(InvalidEmailException.class);
  }

  @DisplayName("null인 경우 예외가 발생한다")
  @Test
  void shouldThrowExceptionForNullEmail() {
    assertThatThrownBy(() -> emailValidator.validate(null))
        .isInstanceOf(InvalidEmailException.class);
  }
}
