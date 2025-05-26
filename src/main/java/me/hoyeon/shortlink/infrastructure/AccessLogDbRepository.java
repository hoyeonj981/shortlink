package me.hoyeon.shortlink.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import me.hoyeon.shortlink.application.AccessLogReader;
import me.hoyeon.shortlink.application.AccessLogWriter;
import me.hoyeon.shortlink.application.CountryStatisticDto;
import me.hoyeon.shortlink.application.DailyStatisticDto;
import me.hoyeon.shortlink.application.RedirectInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
  public List<DailyStatisticDto> findDailyStatByAlias(
      String urlAlias, LocalDateTime from, LocalDateTime to
  ) {
    return List.of();
  }

  @Override
  public List<CountryStatisticDto> findCountryStatByAlias(
      String urlAlias, LocalDateTime from, LocalDateTime to, int limit
  ) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by("date").ascending());
    return List.of();
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
