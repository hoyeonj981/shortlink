package me.hoyeon.shortlink.domain;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortenedUrlTest {

  private static final Instant STANDARD_TIME = Instant.parse("2024-02-02T10:00:00Z");
  private static final Clock STANDARD_CLOCK = Clock.fixed(STANDARD_TIME, systemDefault());

  @Mock
  private AliasGenerator generator;

  @DisplayName("원본 url가 주어지면 단축 url을 생성한다")
  @Test
  void createShortenedUrlFromOriginalUrl() {
    var givenId = 1L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl = ShortenedUrl.create(
        givenId, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThat(shortenedUrl.getOriginalUrlToString()).isEqualTo(givenOriginalUrl);
    assertThat(shortenedUrl.getAliasToString()).isEqualTo(givenAlias.getValue());
  }

  @DisplayName("단축 url에 접근할 수 있다면 만료일을 수정할 수 있다")
  @Test
  void shouldUpdateExpirationTimeIfShortenedUrlIsAccessible() {
    var givenId = 1L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    var pastTime = Clock.fixed(
        STANDARD_TIME.minus(1, ChronoUnit.DAYS),
        systemDefault()
    );
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl = ShortenedUrl.create(
        givenId, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThatCode(() -> shortenedUrl.updateExpirationDays(givenDays, pastTime))
        .doesNotThrowAnyException();
  }

  @DisplayName("단축 url에 접근이 가능하지 않다면 만료일을 수정할 때 예외가 발생한다")
  @Test
  void shouldNotUpdateExpirationTimeIfShortenedUrlIsNotAccessible() {
    var givenId = 1L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    var futureTime = Clock.fixed(
        STANDARD_TIME.plus(1, ChronoUnit.DAYS),
        systemDefault()
    );
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl = ShortenedUrl.create(
        givenId, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThatThrownBy(() -> shortenedUrl.updateExpirationDays(givenDays + 1, futureTime))
        .isInstanceOf(InvalidUrlStateException.class);
  }

  @DisplayName("만료시간에 도달했다면 단축 url에 접근할 수 없다")
  @Test
  void shouldNotBeAccessibleIfTimeIsExpired() {
    var givenId = 1L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    var futureTime = Clock.fixed(
        STANDARD_TIME.plus(1, ChronoUnit.DAYS),
        systemDefault()
    );
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl = ShortenedUrl.create(
        givenId, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThat(shortenedUrl.isAccessible(futureTime)).isFalse();
  }

  @DisplayName("만료시간이 남았다면 단축 url에 접근할 수 있다")
  @Test
  void shouldBeAccessibleIfTimeIsNotExpired() {
    var givenId = 1L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    var pastTime = Clock.fixed(
        STANDARD_TIME.minus(1, ChronoUnit.DAYS),
        systemDefault()
    );
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl = ShortenedUrl.create(
        givenId, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThat(shortenedUrl.isAccessible(pastTime)).isTrue();
  }

  @DisplayName("id 값이 같다면 동일한 객체이다")
  @Test
  void sameObjectIfIdValueIsSame() {
    var givenId1 = 1L;
    var givenId2 = 1L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl1 = ShortenedUrl.create(
        givenId1, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);
    var shortenedUrl2 = ShortenedUrl.create(
        givenId2, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThat(givenId1).isEqualTo(givenId2);
    assertThat(shortenedUrl1).isEqualTo(shortenedUrl2);
  }

  @DisplayName("id 값이 다르다면 다른 객체이다")
  @Test
  void differentObjectIfIdValueIsDifferent() {
    var givenId1 = 1L;
    var givenId2 = 2L;
    var givenOriginalUrl = "https://example.com";
    var givenDays = 1;
    var givenAlias = new Alias("abcdef");
    when(generator.shorten(anyString())).thenReturn(givenAlias);

    var shortenedUrl1 = ShortenedUrl.create(
        givenId1, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);
    var shortenedUrl2 = ShortenedUrl.create(
        givenId2, givenOriginalUrl, generator, givenDays, STANDARD_CLOCK);

    assertThat(givenId1).isNotEqualTo(givenId2);
    assertThat(shortenedUrl1).isNotEqualTo(shortenedUrl2);
  }
}
