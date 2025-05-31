package me.hoyeon.shortlink.application;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private final AccessLogReader accessLogReader;

  public StatisticsService(AccessLogReader accessLogReader) {
    this.accessLogReader = accessLogReader;
  }

  public Long getTotalClicks(String urlAlias) {
    return accessLogReader.getTotalCount(urlAlias);
  }

  public List<DailyStatisticDto> getDailyStatistics(
      String urlAlias, LocalDate from, LocalDate to, int page, int limit
  ) {
    return accessLogReader.findDailyStatByAlias(urlAlias, from, to, page, limit);
  }

  public List<CountryStatisticDto> getCountryStatistics(
      String urlAlias, LocalDate from, LocalDate to, int page, int limit
  ) {
    return accessLogReader.findCountryStatByAlias(urlAlias, from, to, page, limit);
  }
}
