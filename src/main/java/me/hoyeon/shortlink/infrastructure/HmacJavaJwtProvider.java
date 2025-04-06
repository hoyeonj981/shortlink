package me.hoyeon.shortlink.infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import me.hoyeon.shortlink.application.AuthenticationException;
import me.hoyeon.shortlink.application.InvalidJwtTokenException;
import me.hoyeon.shortlink.application.JwtTokenProvider;

public class HmacJavaJwtProvider implements JwtTokenProvider {

  private final HmacJwtProperties jwtProperties;
  private final Algorithm algorithm;
  private final Clock clock;

  public HmacJavaJwtProvider(HmacJwtProperties jwtProperties, Clock clock) {
    this.jwtProperties = jwtProperties;
    this.algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
    this.clock = clock;
  }

  private Algorithm selectAlgorithm(String algorithm) {
    return switch (algorithm) {
      case "HS256" -> Algorithm.HMAC256(jwtProperties.getSecret());
      case "HS384" -> Algorithm.HMAC384(jwtProperties.getSecret());
      case "HS512" -> Algorithm.HMAC512(jwtProperties.getSecret());
      default -> throw new IllegalArgumentException("지원하지 않는 알고리즘입니다" + algorithm);
    };
  }

  @Override
  public String generateAccessToken(Long memberId) {
    try {
      Instant expiration = Instant.now(clock)
          .plusSeconds(jwtProperties.getAccessExpiration());
      return JWT.create()
          .withIssuer(jwtProperties.getIssuer())
          .withClaim("memberId", memberId)
          .withExpiresAt(expiration)
          .sign(algorithm);
    } catch (JWTCreationException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }

  @Override
  public String generateRefreshToken(Long memberId) {
    try {
      var expiration = Instant.now(clock).plusSeconds(jwtProperties.getRefreshExpiration());
      var tokenId = UUID.randomUUID().toString();
      return JWT.create()
          .withIssuer(jwtProperties.getIssuer())
          .withClaim("memberId", memberId)
          .withClaim("tokenId", tokenId)
          .withExpiresAt(expiration)
          .sign(algorithm);
    } catch (IllegalArgumentException | JWTCreationException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }

  @Override
  public String refreshAccessToken(String refreshToken) throws InvalidJwtTokenException {
    validate(refreshToken);
    var memberId = JWT.decode(refreshToken).getClaim("memberId").asLong();
    return generateAccessToken(memberId);
  }

  @Override
  public void invalidate(String token) {
  }

  @Override
  public void validate(String token) throws InvalidJwtTokenException {
    try {
      JWT.require(algorithm)
          .withIssuer(jwtProperties.getIssuer())
          .build()
          .verify(token);
    } catch (JWTVerificationException e) {
      throw new InvalidJwtTokenException(e.getMessage(), e);
    }
  }
}
