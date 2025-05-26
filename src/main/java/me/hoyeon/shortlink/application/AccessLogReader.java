package me.hoyeon.shortlink.application;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessLogReader {

  long getTotalCount(String alias);

  List<DailyStatisticDto> findDailyStatByAlias(
      String urlAlias, LocalDateTime from, LocalDateTime to);

  List<CountryStatisticDto> findCountryStatByAlias(
      String urlAlias, LocalDateTime from, LocalDateTime to, int limit);
}
