package me.hoyeon.shortlink.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Clock;
import me.hoyeon.shortlink.application.InvalidJwtTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class HmacJavaJwtProviderTest {

  private static final String MY_SECRET_KEY = "my-secret-key";
  private static final String ISSUER = "issuer";

  @DisplayName("유효한 엑세스 토큰을 생성한다")
  @Test
  void generateValidAccessToken() {
    var jwtProperties = mock(HmacJwtProperties.class);
    when(jwtProperties.getSecret()).thenReturn(MY_SECRET_KEY);
    when(jwtProperties.getAlgorithm()).thenReturn("HS256");
    when(jwtProperties.getIssuer()).thenReturn(ISSUER);
    when(jwtProperties.getAccessExpiration()).thenReturn(3600000L);
    var jwtProvider = new HmacJavaJwtProvider(jwtProperties, Clock.systemDefaultZone());
    var memberId = 1L;

    var token = jwtProvider.generateAccessToken(memberId);
    assertThat(token).isNotNull();

    var decodedJwt = JWT.require(Algorithm.HMAC256(MY_SECRET_KEY))
        .withIssuer(ISSUER)
        .build()
        .verify(token);
    assertThat(decodedJwt.getIssuer()).isEqualTo(ISSUER);
    assertThat(decodedJwt.getClaim("memberId").asLong()).isEqualTo(memberId);
  }

  @DisplayName("JWT 생성 실패할 경우 예외가 발생한다")
  @Test
  void throwAuthenticationExceptionIfJwtCreationFails() {
    var jwtProperties = mock(HmacJwtProperties.class);
    when(jwtProperties.getSecret()).thenReturn(MY_SECRET_KEY);
    when(jwtProperties.getAlgorithm()).thenReturn("INVALID-ALGORITHM");
    when(jwtProperties.getIssuer()).thenReturn(ISSUER);
    when(jwtProperties.getAccessExpiration()).thenReturn(3600000L);

    assertThatThrownBy(() -> new HmacJavaJwtProvider(jwtProperties,  Clock.systemDefaultZone()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("유효한 토큰을 검증한다")
  @Test
  void validateValidToken() {
    var jwtProperties = mock(HmacJwtProperties.class);
    when(jwtProperties.getSecret()).thenReturn(MY_SECRET_KEY);
    when(jwtProperties.getAlgorithm()).thenReturn("HS256");
    when(jwtProperties.getIssuer()).thenReturn(ISSUER);
    when(jwtProperties.getAccessExpiration()).thenReturn(3600000L);
    var jwtProvider = new HmacJavaJwtProvider(jwtProperties, Clock.systemDefaultZone());
    var validToken = jwtProvider.generateAccessToken(1L);

    assertThatCode(() -> jwtProvider.validate(validToken))
        .doesNotThrowAnyException();
  }

  @DisplayName("잘못된 서명으로 서명된 토큰은 예외를 발생시킨다")
  @Test
  void throwExceptionForInvalidSignatureToken() {
    var jwtProperties = mock(HmacJwtProperties.class);
    when(jwtProperties.getSecret()).thenReturn(MY_SECRET_KEY);
    when(jwtProperties.getAlgorithm()).thenReturn("HS256");
    when(jwtProperties.getIssuer()).thenReturn(ISSUER);
    when(jwtProperties.getAccessExpiration()).thenReturn(3600000L);
    var jwtProvider = new HmacJavaJwtProvider(jwtProperties, Clock.systemDefaultZone());

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
    var jwtProperties = mock(HmacJwtProperties.class);
    when(jwtProperties.getSecret()).thenReturn(MY_SECRET_KEY);
    when(jwtProperties.getAlgorithm()).thenReturn("HS256");
    when(jwtProperties.getIssuer()).thenReturn(ISSUER);
    when(jwtProperties.getAccessExpiration()).thenReturn(-1L);
    var jwtProvider = new HmacJavaJwtProvider(jwtProperties, Clock.systemDefaultZone());

    var expiredToken = jwtProvider.generateAccessToken(1L);

    assertThatThrownBy(() -> jwtProvider.validate(expiredToken))
        .isInstanceOf(InvalidJwtTokenException.class);
  }
}