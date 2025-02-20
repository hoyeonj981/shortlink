package me.hoyeon.shortlink.application;

import java.time.Clock;
import me.hoyeon.shortlink.domain.ShortenedUrl;
import me.hoyeon.shortlink.domain.SimpleAliasGenerator;

public class ShortenedUrlCreator {

  private static final int DEFAULT_EXPIRATION_DAYS = 7;

  private final SimpleAliasGenerator aliasGenerator;
  private final Clock systemClock;
  private final SimpleIdGenerator idGenerator;

  public ShortenedUrlCreator(
      SimpleAliasGenerator aliasGenerator,
      Clock systemClock,
      SimpleIdGenerator idGenerator
  ) {
    this.aliasGenerator = aliasGenerator;
    this.systemClock = systemClock;
    this.idGenerator = idGenerator;
  }

  public ShortenedUrl create(String originalUrl) {
    var alias = aliasGenerator.shorten(originalUrl);
    return ShortenedUrl.create(
        idGenerator.getId(),
        originalUrl,
        alias,
        DEFAULT_EXPIRATION_DAYS,
        systemClock
    );
  }
}
