package me.hoyeon.shortlink.application;

import java.time.LocalDate;

public record DailyStatisticDto(
    LocalDate date,
    long count
) {

}
