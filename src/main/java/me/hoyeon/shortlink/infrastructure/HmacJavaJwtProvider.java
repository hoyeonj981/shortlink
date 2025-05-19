package me.hoyeon.shortlink.infrastructure;

import static me.hoyeon.shortlink.domain.MemberVerificationStatus.of;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.MEMBER_ID;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.ROLE;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.TOKEN_TYPE;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import me.hoyeon.shortlink.application.AuthenticationException;
import me.hoyeon.shortlink.application.InvalidJwtTokenException;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.application.MemberQueryService;
import me.hoyeon.shortlink.domain.Member;
import me.hoyeon.shortlink.infrastructure.config.HmacJwtProperties;

public class HmacJavaJwtProvider implements JwtTokenProvider {

  private static final String REFRESH = "refresh";

  private final HmacJwtProperties jwtProperties;
  private final Clock clock;
  private final JwtRepository jwtRepository;
  private final MemberQueryService memberQueryService;

  public HmacJavaJwtProvider(
      HmacJwtProperties jwtProperties,
      Clock clock,
      JwtRepository jwtRepository,
      MemberQueryService memberQueryService
  ) {
    this.jwtProperties = jwtProperties;
    this.clock = clock;
    this.jwtRepository = jwtRepository;
    this.memberQueryService = memberQueryService;
  }

  @Override
  public String generateAccessToken(Member member) {
    try {
      var algorithm = selectAlgorithm(jwtProperties.getAlgorithm());
      var now = Instant.now(clock);
      var expiration = now.plusSeconds(jwtProperties.getAccessExpiration());
      var tokenId = UUID.randomUUID().toString();
      var memberId = member.getId();
      var role = of(member).getValue();
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
    var memberId = JWT.decode(refreshToken).getClaim(MEMBER_ID.getClaimName()).asLong();
    return generateAccessToken(memberQueryService.getMemberById(memberId));
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

  @Override
  public String getClaim(String token, String key) {
    try {
      var claim = JWT.decode(token).getClaim(key);
      if (claim.isMissing()) {
        throw new ClaimNotExistException(key);
      }
      return claim.asString();
    } catch (JWTDecodeException e) {
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
