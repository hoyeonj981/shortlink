package me.hoyeon.shortlink.application;

public interface JwtTokenProvider {

  String generateAccessToken(Long memberId);
}
