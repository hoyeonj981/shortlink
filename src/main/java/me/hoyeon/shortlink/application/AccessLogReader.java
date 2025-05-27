package me.hoyeon.shortlink.application;

import java.time.LocalDate;
import java.util.List;

public interface AccessLogReader {

  long getTotalCount(String alias);

  List<DailyStatisticDto> findDailyStatByAlias(
      String urlAlias, LocalDate from, LocalDate to, int page, int limit);

  List<CountryStatisticDto> findCountryStatByAlias(
      String urlAlias, LocalDate from, LocalDate to, int page, int limit);
}
