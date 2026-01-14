package me.hoyeon.shortlink.integration.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import me.hoyeon.shortlink.infrastructure.BlackListedTokenJpaRepository;
import me.hoyeon.shortlink.infrastructure.DbJwtRepository;
import me.hoyeon.shortlink.integration.support.FixedClockConfig;
import me.hoyeon.shortlink.integration.support.JpaTestConfig;
import me.hoyeon.shortlink.integration.support.TestContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({FixedClockConfig.class, JpaTestConfig.class, TestContainerConfig.class})
class DbJwtRepositoryTest {

  @Autowired
  private BlackListedTokenJpaRepository blackListedTokenRepository;

  @Autowired
  private Clock fixedClock;

  private DbJwtRepository jwtRepository;

  @BeforeEach
  void setUp() {
    jwtRepository = new DbJwtRepository(blackListedTokenRepository, fixedClock);
  }

  @DisplayName("주어진 토큰을 블랙리스트에 등록한다")
  @Test
  void addToBlackListWhenTokenIsGiven() {
    var token = "test-token";
    var expiredTime = Instant.now(fixedClock).plusSeconds(1000).toEpochMilli();

    jwtRepository.addToBlackList(token, expiredTime);

    assertThat(blackListedTokenRepository.existsByToken(token)).isTrue();
  }

  @DisplayName("주어진 토큰이 블랙리스트에 존재하지 않는다면 false를 반환한다")
  @Test
  void returnFalseIfTokenIsNotBlackListed() {
    var token = "test-token";

    assertThat(jwtRepository.isBlackListed(token)).isFalse();
  }

  @DisplayName("주어진 토큰이 블랙리스트에 존재한다면 true를 반환한다")
  @Test
  void returnTrueIfTokenIsBlackListed() {
    var token = "test-token";
    jwtRepository.addToBlackList(token, Instant.now(fixedClock).plusSeconds(1000).toEpochMilli());

    assertThat(jwtRepository.isBlackListed(token)).isTrue();
  }
}
