package me.hoyeon.shortlink.application;

public interface JwtTokenProvider {

  String generateAccessToken(Long memberId);

  String generateRefreshToken(Long memberId);

  String refreshAccessToken(String refreshToken) throws InvalidJwtTokenException;

  void invalidate(String token);

  void validate(String token) throws InvalidJwtTokenException;
}
