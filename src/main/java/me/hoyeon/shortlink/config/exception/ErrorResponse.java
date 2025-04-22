package me.hoyeon.shortlink.config.exception;

public record ErrorResponse(
    String code,
    String message
) {
}
