package me.hoyeon.shortlink.application;

public record SignInResponse(
    String accessToken,
    String refreshToken
) {
}
