package me.hoyeon.shortlink.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record DailyStatisticDto(
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date,
    long count
) {

}
