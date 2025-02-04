package me.hoyeon.shortlink.domain;

import java.time.Clock;
import java.util.Objects;

public class ShortenedUrl {

  private ShortenedUrlId urlId;
  private OriginalUrl originalUrl;
  private Alias alias;
  private ExpirationTime expirationTime;

  public static ShortenedUrl create(
      Long id,
      String url,
      AliasGenerator generator,
      int expirationDays,
      Clock clock
  ) {
    var urlId = new ShortenedUrlId(id);
    var originalUrl = new OriginalUrl(url);
    var alias = generator.shorten(url);
    var expirationTime = ExpirationTime.fromNowTo(expirationDays, clock);

    return new ShortenedUrl(
        urlId,
        originalUrl,
        alias,
        expirationTime
    );
  }

  private ShortenedUrl(
      ShortenedUrlId urlId,
      OriginalUrl originalUrl,
      Alias alias,
      ExpirationTime expirationTime
  ) {
    this.urlId = urlId;
    this.originalUrl = originalUrl;
    this.alias = alias;
    this.expirationTime = expirationTime;
  }

  public void updateExpirationDays(int days, Clock clock) {
    if (!this.isAccessible(clock)) {
      throw new InvalidUrlStateException();
    }
    this.expirationTime = ExpirationTime.fromNowTo(days, clock);
  }

  public boolean isAccessible(Clock clock) {
    return !this.expirationTime.isExpired(clock);
  }

  public String getAliasToString() {
    return this.alias.getValue();
  }

  public String getOriginalUrlToString() {
    return this.originalUrl.getValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShortenedUrl that = (ShortenedUrl) o;
    return Objects.equals(urlId, that.urlId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(urlId);
  }
}
