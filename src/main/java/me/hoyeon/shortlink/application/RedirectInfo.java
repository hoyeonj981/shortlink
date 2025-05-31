package me.hoyeon.shortlink.application;

import java.time.LocalDateTime;

public record RedirectInfo(
    String alias,
    String originalUrl,
    String ip,
    String userAgent,
    String referer,
    LocalDateTime redirectedAt
) {

  public RedirectInfo(
      String alias,
      String originalUrl,
      String ip,
      String userAgent,
      String referer
  ) {
    this(alias, originalUrl, ip, userAgent, referer, LocalDateTime.now());
  }

  public RedirectInfo {}
}
