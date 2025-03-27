package me.hoyeon.shortlink.application;

public interface JwtTokenProvider {

  String generateAccessToken(Long memberId);

  void invalidate(String token);

  void validate(String token) throws InvalidJwtTokenException;
}
