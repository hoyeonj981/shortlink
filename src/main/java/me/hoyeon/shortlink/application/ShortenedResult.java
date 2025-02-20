package me.hoyeon.shortlink.application;

public record ShortenedResult(
    String originalUrl,
    String shortenedUrl
) {}
