package me.hoyeon.shortlink.application;

import java.util.Map;
import me.hoyeon.shortlink.domain.Member;

public interface JwtTokenProvider {

  String generateAccessToken(Long memberId);

  String generateAccessToken(Map<String, ?> claims);

  String generateAccessToken(Member member);

  String generateRefreshToken(Long memberId);

  String generateRefreshToken(Map<String, ?> claims);

  String generateRefreshToken(Member member);

  String refreshAccessToken(String refreshToken) throws InvalidJwtTokenException;

  void invalidate(String token);

  void validate(String token) throws InvalidJwtTokenException;
}
