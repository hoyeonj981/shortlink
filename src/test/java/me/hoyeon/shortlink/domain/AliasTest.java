package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AliasTest {

  @DisplayName("유효한 문자로만 구성된 경우 Alias 객체가 생성된다")
  @ParameterizedTest
  @ValueSource(strings = {
      "abcdef",
      "ABCDEF",
      "123456",
      "aB3cD4",
      "a1B2C3",
      "1a2B3C"
  })
  void createAliasWhenContainsValidCharacter(String given) {
    var alias = new Alias(given);

    assertThat(alias.getValue()).isEqualTo(given);
  }

  @DisplayName("Alias의 길이가 6이 아니라면 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(strings = {
      "a",
      "ab",
      "abc",
      "abcd",
      "abcde",
      "adbcdef"
  })
  void lengthOfTheAliasValueIs6(String given) {
    assertThatThrownBy(() -> new Alias(given))
        .isInstanceOf(InvalidAliasException.class);
  }

  @DisplayName("Alias는 영어 대문자, 소문자, 숫자가 아니라면 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(strings = {
      "abc!de",
      "abc@de",
      "abc#de",
      "abc$de",
      "abc%de",
      "abc^de",
      "abc&de",
      "abc*de",
      "abc(de",
      "abc)de",
      "abc de",
      "abc-de",
      "abc_de",
      "반갑습니다람",
      "αβγδεζ",
      "あいうえお",
      "абвгде",
  })
  void shouldContainOnlyUppercaseLowercaseAndDigitsInGeneratedAlias(String given) {
    assertThatThrownBy(() -> new Alias(given))
        .isInstanceOf(InvalidAliasException.class);
  }

  @DisplayName("값이 Null이거나 비어있을 경우 예외가 발생한다")
  @ParameterizedTest
  @NullAndEmptySource
  void throwExceptionIfValueIsNullOrEmpty(String given) {
    assertThatThrownBy(() -> new Alias(given))
        .isInstanceOf(InvalidAliasException.class);
  }
}
