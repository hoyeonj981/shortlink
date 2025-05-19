package me.hoyeon.shortlink.infrastructure.config;

public record ErrorResponse(
    String code,
    String message
) {
}
