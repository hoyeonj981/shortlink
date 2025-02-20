package me.hoyeon.shortlink.application;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import me.hoyeon.shortlink.domain.Alias;
import me.hoyeon.shortlink.domain.SimpleAliasGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortenedUrlCreatorTest {

  private static final Instant STANDARD_TIME = Instant.parse("2024-02-02T10:00:00Z");
  private static final Clock STANDARD_CLOCK = Clock.fixed(STANDARD_TIME, systemDefault());

  private SimpleIdGenerator idGenerator;

  private SimpleAliasGenerator aliasGenerator;

  private ShortenedUrlCreator creator;

  @BeforeEach
  void setUp() {
    idGenerator = mock(SimpleIdGenerator.class);
    aliasGenerator = mock(SimpleAliasGenerator.class);
    creator = new ShortenedUrlCreator(
        aliasGenerator,
        STANDARD_CLOCK,
        idGenerator
    );
  }

  @DisplayName("원본 URL로 단축 URL을 생성한다")
  @Test
  void createShortenedUrl() {
    var givenOriginalUrl = "https://example.com";
    var givenId = 1L;
    var givenAlias = "abcdef";
    var expectedAlias = new Alias(givenAlias);
    when(aliasGenerator.shorten(anyString())).thenReturn(expectedAlias);
    when(idGenerator.getId()).thenReturn(givenId);

    var result = creator.create(givenOriginalUrl);

    assertThat(result.getOriginalUrlToString()).isEqualTo(givenOriginalUrl);
    assertThat(result.getAliasToString()).isEqualTo(givenAlias);
  }
}