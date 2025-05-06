package me.hoyeon.shortlink.unit.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.hoyeon.shortlink.application.AuthenticationException;
import me.hoyeon.shortlink.application.InvalidJwtTokenException;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.infrastructure.HmacJavaJwtProvider;
import me.hoyeon.shortlink.infrastructure.HmacJwtProperties;
import me.hoyeon.shortlink.infrastructure.JwtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class HmacJavaJwtProviderTest {

  private static final String MY_SECRET_KEY = "my-secret-key";
  private static final String ISSUER = "issuer";
  private static final String EMPTY_SECRET = "";

  private JwtTokenProvider jwtProvider;

  private JwtRepository jwtRepository;

  private HmacJwtProperties jwtProperties;

  @BeforeEach
  void setUp() {
    jwtProperties = new HmacJwtProperties();
    jwtProperties.setSecret(MY_SECRET_KEY);
    jwtProperties.setAlgorithm("HS256");
    jwtProperties.setIssuer(ISSUER);
    jwtProperties.setAccessExpiration(3600000L);
    jwtProperties.setRefreshExpiration(7200000L);

    jwtRepository = mock(JwtRepository.class);
    jwtProvider = new HmacJavaJwtProvider(
        jwtProperties,
        Clock.systemDefaultZone(),
        jwtRepository);
  }

  @DisplayName("유효한 엑세스 토큰을 생성한다")
  @Test
  void generateValidAccessToken() {
    var memberId = 1L;

    var token = jwtProvider.generateAccessToken(memberId);
    var decodedJwt = JWT.require(Algorithm.HMAC256(MY_SECRET_KEY))
        .withIssuer(ISSUER)
        .build()
        .verify(token);

    assertThat(token).isNotNull();
    assertThat(decodedJwt.getIssuer()).isEqualTo(ISSUER);
    assertThat(decodedJwt.getClaim("memberId").asLong()).isEqualTo(memberId);
  }

  @DisplayName("액세스 토큰 생성 시 JWT 생성 실패할 경우 예외가 발생한다")
  @Test
  void throwAuthenticationExceptionIfJwtCreationFails() {
    var memberId = 1L;
    jwtProperties.setSecret(EMPTY_SECRET);

    assertThatThrownBy(() -> jwtProvider.generateAccessToken(memberId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("유효한 토큰을 검증한다")
  @Test
  void validateValidToken() {
    var validToken = jwtProvider.generateAccessToken(1L);

    assertThatCode(() -> jwtProvider.validate(validToken))
        .doesNotThrowAnyException();
  }

  @DisplayName("잘못된 서명으로 서명된 토큰은 예외를 발생시킨다")
  @Test
  void throwExceptionForInvalidSignatureToken() {
    var tokenWithWrongSignature = JWT.create()
        .withIssuer(ISSUER)
        .withClaim("memberId", 1L)
        .sign(Algorithm.HMAC256("wrong-secret-key"));

    assertThatThrownBy(() -> jwtProvider.validate(tokenWithWrongSignature))
        .isInstanceOf(InvalidJwtTokenException.class);
  }

  @DisplayName("만료된 토큰은 예외를 발생시킨다")
  @Test
  void throwExceptionForExpiredToken() {
    jwtProperties.setAccessExpiration(-1L);

    var expiredToken = jwtProvider.generateAccessToken(1L);

    assertThatThrownBy(() -> jwtProvider.validate(expiredToken))
        .isInstanceOf(InvalidJwtTokenException.class);
  }

  @DisplayName("유효한 리프레시 토큰을 생성한다")
  @Test
  void createValidRefreshToken() {
    var memberId = 1L;

    var refreshToken = jwtProvider.generateRefreshToken(memberId);
    var decodedJwt = JWT.require(Algorithm.HMAC256(MY_SECRET_KEY))
        .withIssuer(ISSUER)
        .build()
        .verify(refreshToken);

    assertThat(refreshToken).isNotNull();
    assertThat(decodedJwt.getIssuer()).isEqualTo(ISSUER);
    assertThat(decodedJwt.getClaim("memberId").asLong()).isEqualTo(memberId);
  }

  @DisplayName("리프레시 토큰 생성 시 JWT 생성에 실패할 경우 예외가 발생한다")
  @Test
  void throwAuthenticationExceptionIfJwtCreationFailsWhenCreatingRefreshToken() {
    var memberId = 1L;
    jwtProperties.setSecret(EMPTY_SECRET);

    assertThatThrownBy(() -> jwtProvider.generateRefreshToken(memberId))
        .isInstanceOf(AuthenticationException.class);
  }

  @DisplayName("JWT가 블랙리스트에 없다면 블랙리스트에 추가한다")
  @Test
  void addJwtToBlackListIfItIsNotInBlackList() {
    when(jwtRepository.isBlackListed(anyString())).thenReturn(false);
    var givenToken = createTestJwtToken(3600000L);

    jwtProvider.invalidate(givenToken);

    verify(jwtRepository).isBlackListed(givenToken);
    verify(jwtRepository).addToBlackList(eq(givenToken), anyLong());
  }

  @DisplayName("JWT가 이미 블랙리스트에 등록되었다면 아무 동작을 수행하지 않는다")
  @Test
  void doNothingIfJwtIsInBlackList() {
    when(jwtRepository.isBlackListed(anyString())).thenReturn(true);
    var givenToken = createTestJwtToken(3600000L);

    jwtProvider.invalidate(givenToken);

    verify(jwtRepository).isBlackListed(givenToken);
    verify(jwtRepository, never()).addToBlackList(eq(givenToken), anyLong());
  }

  private String createTestJwtToken(long expiresInSeconds) {
    var algorithm = Algorithm.HMAC256(MY_SECRET_KEY);
    var expiresAt = Date.from(Instant.now().plusSeconds(expiresInSeconds));

    return JWT.create()
        .withIssuer(ISSUER)
        .withExpiresAt(expiresAt)
        .sign(algorithm);
  }

  @Test
  @DisplayName("claims 맵 기반으로 access token을 발급하면 각 claim이 정상적으로 포함된다")
  void generateAccessTokenWithClaimsTest() {
    var claims = new HashMap<String, Object>();
    claims.put("memberId", 1234L);
    claims.put("username", "testUser");
    claims.put("isAdmin", true);
    claims.put("age", 27);

    var token = jwtProvider.generateAccessToken(claims);
    var decoded = JWT.decode(token);

    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
    assertThat(decoded.getClaim("memberId").asLong()).isEqualTo(1234L);
    assertThat(decoded.getClaim("username").asString()).isEqualTo("testUser");
    assertThat(decoded.getClaim("isAdmin").asBoolean()).isTrue();
    assertThat(decoded.getClaim("age").asInt()).isEqualTo(27);
    assertThat(decoded.getExpiresAt()).isNotNull();
  }

  @Test
  @DisplayName("claims 맵 기반으로 refresh token을 발급하면 각 claim이 정상적으로 포함된다")
  void generateRefreshTokenWithClaimsTest() {
    var claims = new HashMap<String, Object>();
    claims.put("memberId", 5678L);
    claims.put("tokenType", "refresh");

    var token = jwtProvider.generateRefreshToken(claims);
    var decoded = JWT.decode(token);

    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
    assertThat(decoded.getClaim("memberId").asLong()).isEqualTo(5678L);
    assertThat(decoded.getClaim("tokenType").asString()).isEqualTo("refresh");
    assertThat(decoded.getExpiresAt()).isNotNull();
  }
}