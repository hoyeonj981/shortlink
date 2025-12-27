package me.hoyeon.shortlink.domain;

public record UrlRiskScore(
    double technical,
    double content,
    double behavior
) {}
