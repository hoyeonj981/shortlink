package me.hoyeon.shortlink.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyStatisticDto(
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date,
    Long count
) {
  public DailyStatisticDto(LocalDateTime date, Long count) {
    this(date.toLocalDate(), count);
  }
}
