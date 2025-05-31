package me.hoyeon.shortlink.infrastructure;

import java.time.LocalDate;
import java.util.List;
import me.hoyeon.shortlink.application.CountryStatisticDto;
import me.hoyeon.shortlink.application.DailyStatisticDto;
import org.springframework.data.domain.Pageable;

public interface AccessLogQueryRepository {

  List<DailyStatisticDto> findDailyStatByAlias(
      String alias, LocalDate from, LocalDate to, Pageable pageable);

  List<CountryStatisticDto> findCountryStatByAlias(
      String alias, LocalDate from, LocalDate to, Pageable pageable);
}
