package me.hoyeon.shortlink.infrastructure;

import me.hoyeon.shortlink.application.AccessLogReader;
import me.hoyeon.shortlink.application.AccessLogWriter;
import me.hoyeon.shortlink.application.RedirectInfo;

public class AccessLogDbRepository implements AccessLogWriter, AccessLogReader {

  private final AccessLogJpaRepository jpaRepository;

  public AccessLogDbRepository(AccessLogJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public long getTotalCount(String alias) {
    return jpaRepository.countByAlias(alias);
  }

  @Override
  public void write(RedirectInfo info) {
    var accessLog = new AccessLog();
    accessLog.setAlias(info.alias());
    accessLog.setOriginalUrl(info.originalUrl());
    accessLog.setUserAgent(info.userAgent());
    accessLog.setIp(info.ip());
    accessLog.setReferer(info.referer());
    accessLog.setRedirectedAt(info.redirectedAt());
    jpaRepository.save(accessLog);
  }
}
