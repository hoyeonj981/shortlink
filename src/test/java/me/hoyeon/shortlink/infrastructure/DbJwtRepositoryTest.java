package me.hoyeon.shortlink.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DbJwtRepositoryTest {

  private BlackListedTokenJpaRepository jpaRepository;

  private Clock clock;

  private DbJwtRepository dbJwtRepository;

  @BeforeEach
  void setUp() {
    jpaRepository = mock(BlackListedTokenJpaRepository.class);
    clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.systemDefault());
    dbJwtRepository = new DbJwtRepository(jpaRepository, clock);
  }

  @DisplayName("주어진 토큰을 블랙리스트에 등록한다")
  @Test
  void addToBlackListWhenTokenIsGiven() {
    var token = "test-token";
    var expireTime = Instant.parse("2023-01-02T00:00:00Z").toEpochMilli();

    dbJwtRepository.addToBlackList(token, expireTime);

    var captor = ArgumentCaptor.forClass(BlackListedToken.class);
    verify(jpaRepository).save(captor.capture());

    var savedToken = captor.getValue();
    assertThat(savedToken.getToken()).isEqualTo(token);
    assertThat(savedToken.getExpiredAt()).isEqualTo(LocalDateTime.ofInstant(
        Instant.ofEpochMilli(expireTime), ZoneId.systemDefault()));
    assertThat(savedToken.getCreatedAt()).isEqualTo(LocalDateTime.now(clock));
  }

  @DisplayName("주어진 토큰이 블랙리스트에 존재하면 true를 반환한다")
  @Test
  void returnTrueIfTokenIsBlackListed() {
    var token = "test-token";
    when(jpaRepository.existsByToken(token)).thenReturn(true);

    var result = dbJwtRepository.isBlackListed(token);

    assertThat(result).isTrue();
    verify(jpaRepository).existsByToken(token);
  }

  @DisplayName("주어진 토큰이 블랙리스트에 존재하면 false를 반환한다")
  @Test
  void returnFalseIfTokenIsNotBlackListed() {
    var token = "test-token";
    when(jpaRepository.existsByToken(token)).thenReturn(false);

    var result = dbJwtRepository.isBlackListed(token);

    assertThat(result).isFalse();
    verify(jpaRepository).existsByToken(token);
  }
}