package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OriginalUrlTest {

  @DisplayName("URL 값이 null이거나 비어있다면 예외가 발생한다")
  @ParameterizedTest
  @NullAndEmptySource
  void throwExceptionIfValueIsNullOrEmpty(String givenUrl) {
    assertThatThrownBy(() -> new OriginalUrl(givenUrl))
        .isInstanceOf(InvalidUrlException.class);
  }

  @DisplayName("상대 참조는 유효한 형식이 아니다")
  @ParameterizedTest
  @ValueSource(strings = {
      "/hello",
      "/hello world/",
      "../hello/world",
      "/hello//world",
      "./hello/world"
  })
  void relativeReferenceShouldNotBeAllowed(String givenUrl) {
    assertThatThrownBy(() -> new OriginalUrl(givenUrl))
        .isInstanceOf(InvalidUrlException.class);
  }

  @DisplayName("http, https 스키마가 아니라면 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(strings = {
      "file:///C:/document.txt",
      "mailto:user@example.com",
      "tel:+1234567890",
      "sms:+1234567890",
      "ftp://ftp.example.com/file.txt",
      "sftp://example.com/file",
      "ssh://user@host.example.com",
      "data:text/plain,Hello",
      "javascript:alert('hello')",
      "about:blank"
  })
  void throwExceptionIfSchemeIsNotHttpOrHttps(String givenUrl) {
    assertThatThrownBy(() -> new OriginalUrl(givenUrl))
        .isInstanceOf(NotAllowedSchemeException.class);
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
