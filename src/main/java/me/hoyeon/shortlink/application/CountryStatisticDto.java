package me.hoyeon.shortlink.application;

public record CountryStatisticDto(
    String countryCode,
    String countryName,
    long count
) {

}
