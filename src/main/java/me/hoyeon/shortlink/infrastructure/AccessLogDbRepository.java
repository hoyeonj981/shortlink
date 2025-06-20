package me.hoyeon.shortlink.infrastructure;

import java.time.LocalDate;
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

  private static final String DATE = "date";

  private final AccessLogJpaRepository jpaRepository;
  private final IpGeoLocationProvider ipGeoLocationProvider;

  public AccessLogDbRepository(
      AccessLogJpaRepository jpaRepository,
      IpGeoLocationProvider ipGeoLocationProvider
  ) {
    this.jpaRepository = jpaRepository;
    this.ipGeoLocationProvider = ipGeoLocationProvider;
  }

  @Override
  public long getTotalCount(String alias) {
    return jpaRepository.countByAlias(alias);
  }

  @Override
  public List<DailyStatisticDto> findDailyStatByAlias(
      String urlAlias, LocalDate from, LocalDate to, int page, int limit
  ) {
    Pageable pageable = PageRequest.of(page, limit, Sort.by(DATE).ascending());
    return jpaRepository.findDailyStatByAlias(urlAlias, from, to, pageable);
  }

  @Override
  public List<CountryStatisticDto> findCountryStatByAlias(
      String urlAlias, LocalDate from, LocalDate to, int page, int limit
  ) {
    Pageable pageable = PageRequest.of(page, limit, Sort.by(DATE).ascending());
    return jpaRepository.findCountryStatByAlias(urlAlias, from, to, pageable);
  }

  @Override
  public void write(RedirectInfo info) {
    var accessLog = new AccessLog();
    accessLog.setAlias(info.alias());
    accessLog.setOriginalUrl(info.originalUrl());
    accessLog.setUserAgent(info.userAgent());
    accessLog.setIp(info.ip());
    accessLog.setCountry(ipGeoLocationProvider.extractCountry(info.ip()));
    accessLog.setReferer(info.referer());
    accessLog.setRedirectedAt(info.redirectedAt());
    jpaRepository.save(accessLog);
  }
}
