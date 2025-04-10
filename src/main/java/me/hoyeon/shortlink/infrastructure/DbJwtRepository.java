package me.hoyeon.shortlink.infrastructure;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

public class DbJwtRepository implements JwtRepository {

  private final BlackListedTokenJpaRepository jpaRepository;
  private final Clock clock;

  public DbJwtRepository(BlackListedTokenJpaRepository jpaRepository, Clock clock) {
    this.jpaRepository = jpaRepository;
    this.clock = clock;
  }

  @Override
  public void addToBlackList(String token, long expireTime) {
    var blackListedToken = new BlackListedToken();
    var expiredAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expireTime), clock.getZone());
    var now = LocalDateTime.now(clock);
    blackListedToken.setToken(token);
    blackListedToken.setExpiredAt(expiredAt);
    blackListedToken.setCreatedAt(now);
    jpaRepository.save(blackListedToken);
  }

  @Override
  public boolean isBlackListed(String token) {
    return jpaRepository.existsByToken(token);
  }
}
