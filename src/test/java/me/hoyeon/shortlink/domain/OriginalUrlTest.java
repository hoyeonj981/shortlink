package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OriginalUrlTest {

  @DisplayName("유효하지 않은 URL 형식은 예외가 발생한다")
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {
      "invalid-url",
      "hello world",
      "http",
      "http:/",
      "https://",
      "ftp:",
      "://",
      "abcd://"
  })
  void throwExceptionIfUrlFormatIsNotValid(String givenUrl) {
    assertThatThrownBy(() -> new OriginalUrl(givenUrl))
        .isInstanceOf(InvalidUrlException.class);
  }

  @DisplayName("동일한 URL 값을 가진 OriginalUrl은 서로 같다")
  @Test
  void originalUrlsWithSameValueAreSame() {
    var givenUrl1 = "https://example.com";
    var givenUrl2 = "https://example.com";

    var url1 = new OriginalUrl(givenUrl1);
    var url2 = new OriginalUrl(givenUrl2);

    assertThat(givenUrl1).isEqualTo(givenUrl2);
    assertThat(url1).isEqualTo(url2);
    assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
  }

  @DisplayName("다른 URL 값을 가진 OriginalUrl은 서로 다르다")
  @Test
  void originalUrlsWithDifferentValueAreDifferent() {
    var givenUrl1 = "https://example.com/a";
    var givenUrl2 = "https://example.com/b";

    var url1 = new OriginalUrl(givenUrl1);
    var url2 = new OriginalUrl(givenUrl2);

    assertThat(givenUrl1).isNotEqualTo(givenUrl2);
    assertThat(url1).isNotEqualTo(url2);
    assertThat(url1.hashCode()).isNotEqualTo(url2.hashCode());
  }
}
