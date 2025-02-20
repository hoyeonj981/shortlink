package me.hoyeon.shortlink.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import me.hoyeon.shortlink.domain.Alias;
import me.hoyeon.shortlink.domain.MutableClock;
import me.hoyeon.shortlink.domain.ShortenedUrl;
import me.hoyeon.shortlink.domain.UrlMappingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

  @Mock
  private UrlMappingRepository repository;

  @Mock
  private ShortenedUrlCreator creator;

  @InjectMocks
  private UrlShortenerService shortenerService;

  @DisplayName("원본 URL을 받아 단축 URL을 생성한다")
  @Test
  void createShortenedUrlFromOriginalUrl() {
    var givenUrl = "https://www.example.com";
    var givenAlias = "abcdef";
    var alias = new Alias(givenAlias);
    var givenId = 1L;
    var shortenedUrl = ShortenedUrl.create(
        givenId,
        givenUrl,
        alias,
        1,
        Clock.systemDefaultZone()
    );
    when(creator.create(any())).thenReturn(shortenedUrl);
    when(repository.isExistingByOriginalUrl(any())).thenReturn(false);

    var shortenedResult = shortenerService.shortenUrl(givenUrl);

    assertThat(shortenedResult.originalUrl()).isEqualTo(givenUrl);
    assertThat(shortenedResult.shortenedUrl()).isEqualTo(givenAlias);
  }

  @DisplayName("Alias로 조회한 뒤 해당하는 원본 URL을 반환한다")
  @Test
  void returnOriginalUrlWhenAliasExists() {
    var givenUrl = "https://www.example.com";
    var givenAlias = "abcdef";
    var alias = new Alias(givenAlias);
    var givenId = 1L;
    var fixedInstant = Instant.parse("2024-02-07T10:00:00Z");
    var fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
    var shortenedUrl = ShortenedUrl.create(
        givenId,
        givenUrl,
        alias,
        1,
        fixedClock
    );
    when(repository.findByAlias(any())).thenReturn(Optional.of(shortenedUrl));

    var originalUrl = shortenerService.getOriginalUrl(givenAlias);

    assertThat(originalUrl).isEqualTo(givenUrl);
  }

  @DisplayName("생성할 때 중복되는 원본 URL이 존재할 경우 예외를 던진다")
  @Test
  void throwExceptionIfDuplicatedOriginalUrlExists() {
    var givenUrl = "https://www.example.com";
    when(repository.isExistingByOriginalUrl(any())).thenReturn(true);

    assertThatThrownBy(() -> shortenerService.shortenUrl(givenUrl))
        .isInstanceOf(DuplicateUrlException.class);
  }

  @DisplayName("Alias에 해당하는 원본 URL이 존재하지 않는다면 예외를 던진다")
  @Test
  void throwExceptionWhenCouldNotFindOriginalUrlFromAlias() {
    var givenAlias = "abcdedf";
    when(repository.findByAlias(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> shortenerService.getOriginalUrl(givenAlias))
        .isInstanceOf(UrlNotFoundException.class);
  }

  @DisplayName("원본 URL에 해당하는 Alias가 유효하지 않는다면 예외를 던진다")
  @Test
  void throwExceptionWhenAliasIsNotAccessible() {
    var givenUrl = "https://www.example.com";
    var givenAlias = "abcdef";
    var alias = new Alias(givenAlias);
    var givenId = 1L;
    var givenDays = 1;
    var currentTime = Instant.parse("2024-02-07T10:00:00Z");
    var mutableClock = new MutableClock(currentTime);
    var shortenedUrl = ShortenedUrl.create(
        givenId,
        givenUrl,
        alias,
        givenDays,
        mutableClock
    );
    doAnswer(invocation -> {
      mutableClock.setInstant(currentTime.plus(givenDays + 1, ChronoUnit.DAYS));
      return Optional.of(shortenedUrl);
    }).when(repository).findByAlias(givenAlias);

    assertThatThrownBy(() -> shortenerService.getOriginalUrl(givenAlias))
        .isInstanceOf(NotAccsibleUrlException.class);
  }
}
