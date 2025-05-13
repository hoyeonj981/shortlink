package me.hoyeon.shortlink.application;

import me.hoyeon.shortlink.domain.Member;

public interface JwtTokenProvider {

  String generateAccessToken(Member member);

  String generateRefreshToken(Member member);

  String refreshAccessToken(String refreshToken) throws InvalidJwtTokenException;

  void invalidate(String token);

  void validate(String token) throws InvalidJwtTokenException;

  String getClaim(String token, String key);
}
