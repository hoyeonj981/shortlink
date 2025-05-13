package me.hoyeon.shortlink.infrastructure;

import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import me.hoyeon.shortlink.application.AuthenticationException;
import me.hoyeon.shortlink.application.InvalidJwtTokenException;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.domain.Member;
import me.hoyeon.shortlink.domain.MemberVerificationStatus;

public class HmacJavaJwtProvider implements JwtTokenProvider {

  private static final String REFRESH = "refresh";

  private final HmacJwtProperties jwtProperties;
  private final Clock clock;
  private final JwtRepository jwtRepository;

  public HmacJavaJwtProvider(
      HmacJwtProperties jwtProperties,
      Clock clock,
      JwtRepository jwtRepository
  ) {
    this.jwtProperties = jwtProperties;
    this.clock = clock;
    this.jwtRepository = jwtRepository;
  }

  @Override
  public String generateAccessToken(Long memberId) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      var expiration = Instant.now(clock)
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
  public String generateAccessToken(Map<String, ?> claims) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      var expiration = Instant.now(clock)
          .plusSeconds(jwtProperties.getAccessExpiration());
      var builder = JWT.create().withIssuer(jwtProperties.getIssuer());

      for (Map.Entry<String, ?> entry : claims.entrySet()) {
        var key = entry.getKey();
        var value = entry.getValue();

        if (value instanceof String) {
          builder.withClaim(key, (String) value);
        } else if (value instanceof Long) {
          builder.withClaim(key, (Long) value);
        } else if (value instanceof Integer) {
          builder.withClaim(key, (Integer) value);
        } else if (value instanceof Double) {
          builder.withClaim(key, (Double) value);
        } else if (value instanceof Boolean) {
          builder.withClaim(key, (Boolean) value);
        }
      }

      builder.withExpiresAt(expiration);
      return builder.sign(algorithm);
    } catch (JWTCreationException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }

  @Override
  public String generateAccessToken(Member member) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      var now = Instant.now(clock);
      var expiration = now.plusSeconds(jwtProperties.getAccessExpiration());
      var tokenId = UUID.randomUUID().toString();
      var memberId = member.getId();
      var role = MemberVerificationStatus.of(member).getValue();
      return JWT.create()
          .withIssuer(jwtProperties.getIssuer())
          .withJWTId(tokenId)
          .withIssuedAt(now)
          .withExpiresAt(expiration)
          .withClaim(MEMBER_ID.getClaimName(), memberId)
          .withClaim(ROLE.getClaimName(), role)
          .sign(algorithm);
    } catch (JWTCreationException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }

  @Override
  public String generateRefreshToken(Long memberId) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
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
  public String generateRefreshToken(Map<String, ?> claims) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      var expiration = Instant.now(clock)
          .plusSeconds(jwtProperties.getAccessExpiration());
      var builder = JWT.create().withIssuer(jwtProperties.getIssuer());

      for (Map.Entry<String, ?> entry : claims.entrySet()) {
        var key = entry.getKey();
        var value = entry.getValue();

        if (value instanceof String) {
          builder.withClaim(key, (String) value);
        } else if (value instanceof Long) {
          builder.withClaim(key, (Long) value);
        } else if (value instanceof Integer) {
          builder.withClaim(key, (Integer) value);
        } else if (value instanceof Double) {
          builder.withClaim(key, (Double) value);
        } else if (value instanceof Boolean) {
          builder.withClaim(key, (Boolean) value);
        }
      }
      builder.withExpiresAt(expiration);
      return builder.sign(algorithm);
    } catch (JWTCreationException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }

  @Override
  public String generateRefreshToken(Member member) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      var now = Instant.now(clock);
      var expiration = now.plusSeconds(jwtProperties.getRefreshExpiration());
      var tokenId = UUID.randomUUID().toString();
      var memberId = member.getId();
      return JWT.create()
          .withIssuer(jwtProperties.getIssuer())
          .withJWTId(tokenId)
          .withIssuedAt(now)
          .withExpiresAt(expiration)
          .withClaim(MEMBER_ID.getClaimName(), memberId)
          .withClaim(TOKEN_TYPE.getClaimName(), REFRESH)
          .sign(algorithm);
    } catch (JWTCreationException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }


  @Override
  public String refreshAccessToken(String refreshToken) throws InvalidJwtTokenException {
    validate(refreshToken);
    var memberId = JWT.decode(refreshToken)
        .getClaim(MEMBER_ID.getClaimName()).asLong();

    return generateAccessToken(memberId);
  }

  @Override
  public void invalidate(String token) {
    validate(token);
    if (!jwtRepository.isBlackListed(token)) {
      var expiresAt = JWT.decode(token).getExpiresAt();
      jwtRepository.addToBlackList(token, expiresAt.getTime());
    }
  }

  @Override
  public void validate(String token) throws InvalidJwtTokenException {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      JWT.require(algorithm)
          .withIssuer(jwtProperties.getIssuer())
          .build()
          .verify(token);
    } catch (IllegalArgumentException | JWTVerificationException e) {
      throw new InvalidJwtTokenException(e.getMessage(), e);
    }
  }

  private Algorithm selectAlgorithm(String algorithm) {
    return switch (algorithm) {
      case "HS256" -> Algorithm.HMAC256(jwtProperties.getSecret());
      case "HS384" -> Algorithm.HMAC384(jwtProperties.getSecret());
      case "HS512" -> Algorithm.HMAC512(jwtProperties.getSecret());
      default -> throw new IllegalArgumentException("지원하지 않는 알고리즘입니다" + algorithm);
    };
  }
}
