package me.hoyeon.shortlink.ui;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import me.hoyeon.shortlink.application.CountryStatisticDto;
import me.hoyeon.shortlink.application.DailyStatisticDto;
import me.hoyeon.shortlink.application.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/statistics")
@RestController
public class UrlStatisticsController {

  private static final String DEFAULT_LIMIT = "10";
  private static final int LIMIT_DAYS = 90;

  private final StatisticsService statisticsService;

  public UrlStatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/{alias}/daily")
  public ResponseEntity<List<DailyStatisticDto>> getDailyStats(
      @PathVariable String alias,
      @RequestParam LocalDateTime from,
      @RequestParam LocalDateTime to
  ) {
    validatePeriod(from, to);
    var dailyStatistics = statisticsService.getDailyStatistics(alias, from, to);
    return ResponseEntity.ok().body(dailyStatistics);
  }

  @GetMapping("/{alias}/country")
  public ResponseEntity<List<CountryStatisticDto>> getCountryStats(
      @PathVariable String alias,
      @RequestParam LocalDateTime from,
      @RequestParam LocalDateTime to,
      @RequestParam(required = false, defaultValue = DEFAULT_LIMIT) int limit
  ) {
    validatePeriod(from, to);
    var countryStatistics = statisticsService.getCountryStatistics(alias, from, to, limit);
    return ResponseEntity.ok().body(countryStatistics);
  }

  private void validatePeriod(LocalDateTime from, LocalDateTime to) {
    validateAfter(from, to);
    validateDays(from, to);
  }

  private void validateAfter(LocalDateTime from, LocalDateTime to) {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 늦을 수 없습니다.");
    }
  }

  private void validateDays(LocalDateTime from, LocalDateTime to) {
    if (Period.between(from.toLocalDate(), to.toLocalDate()).getDays() > LIMIT_DAYS) {
      var message = String.format("조회 기간은 %d일 이내로 설정할 수 있습니다.", LIMIT_DAYS);
      throw new IllegalArgumentException(message);
    }
  }
}
