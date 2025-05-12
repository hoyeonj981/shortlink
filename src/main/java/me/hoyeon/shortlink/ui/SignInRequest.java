package me.hoyeon.shortlink.ui;

public record SignInRequest(
    String email,
    String rawPassword
) {

}
