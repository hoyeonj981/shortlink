package me.hoyeon.shortlink.application;

import java.util.Map;

public interface JwtTokenProvider {

  String generateAccessToken(Long memberId);

  String generateAccessToken(Map<String, ?> claims);

  String generateRefreshToken(Long memberId);

  String generateRefreshToken(Map<String, ?> claims);

  String refreshAccessToken(String refreshToken) throws InvalidJwtTokenException;

  void invalidate(String token);

  void validate(String token) throws InvalidJwtTokenException;
}
