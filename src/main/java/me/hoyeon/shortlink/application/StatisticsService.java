package me.hoyeon.shortlink.application;

import java.time.LocalDateTime;
import java.util.List;

public class StatisticsService {

  private final AccessLogReader accessLogReader;

  public StatisticsService(AccessLogReader accessLogReader) {
    this.accessLogReader = accessLogReader;
  }

  public Long getTotalClicks(String urlAlias) {
    return accessLogReader.getTotalCount(urlAlias);
  }

  public List<DailyStatisticDto> getDailyStatistics(
      String urlAlias, LocalDateTime from, LocalDateTime to
  ) {
    return accessLogReader.findDailyStatByAlias(urlAlias, from, to);
  }

  public List<CountryStatisticDto> getCountryStatistics(
      String urlAlias, LocalDateTime from, LocalDateTime to, int limit
  ) {
    return accessLogReader.findCountryStatByAlias(urlAlias, from, to, limit);
  }
}
