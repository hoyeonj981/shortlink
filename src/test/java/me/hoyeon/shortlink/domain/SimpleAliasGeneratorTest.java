package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimpleAliasGeneratorTest {

  private static final int SIX = 6;
  private static final String VALID_ALIAS_REGEX = "^[a-zA-Z0-9]+$";

  private AliasGenerator generator;

  @BeforeEach
  void setUp() {
    generator = new SimpleAliasGenerator();
  }

  @DisplayName("동일한 base는 같은 Alias 값이 생성된다")
  @Test
  void sameBaseCreatesTheSameAliasValue() {
    var givenBase1 = "https://example.com";
    var givenBase2 = "https://example.com";

    var shortened1 = generator.shorten(givenBase1);
    var shortened2 = generator.shorten(givenBase2);

    assertThat(givenBase1).isEqualTo(givenBase2);
    assertThat(shortened1).isEqualTo(shortened2);
  }

  @DisplayName("서로 다른 base는 다른 Alias 값을 생성한다")
  @Test
  void differentBaseCreatesDifferentAliasValues() {
    var givenBase1 = "https://example.com";
    var givenBase2 = "https://naver.com";

    var shortened1 = generator.shorten(givenBase1);
    var shortened2 = generator.shorten(givenBase2);

    assertThat(givenBase1).isNotEqualTo(givenBase2);
    assertThat(shortened1).isNotEqualTo(shortened2);
  }

  @DisplayName("생성된 Alias 값의 길이는 6이다")
  @Test
  void lengthOfTheAliasValueIs6() {
    var givenBase = "https://example.com";

    var shortened = generator.shorten(givenBase);

    assertThat(shortened.length()).isEqualTo(SIX);
  }

  @DisplayName("생성된 Alias 값은 영어 대문자와 소문자, 숫자만 포함한다")
  @Test
  void AliasValuesContainOnlyEnglishUppercaseAndLowercaseLettersAndDigit() {
    var givenBase = "https://example.com";

    var shortened = generator.shorten(givenBase);

    assertThat(shortened).matches(VALID_ALIAS_REGEX);
  }
}
