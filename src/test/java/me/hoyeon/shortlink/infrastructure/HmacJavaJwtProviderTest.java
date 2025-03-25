package me.hoyeon.shortlink.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

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
    when(jwtProperties.getExpiration()).thenReturn(3600000L);
    var jwtProvider = new HmacJavaJwtProvider(jwtProperties, Clock.systemDefaultZone());
    var memberId = 1L;

    var token = jwtProvider.generateAccessToken(memberId);
    var decodedJWT = JWT.require(Algorithm.HMAC256(MY_SECRET_KEY))
        .withIssuer(ISSUER)
        .build()
        .verify(token);

    assertThat(token).isNotNull();
    assertThat(decodedJWT.getIssuer()).isEqualTo(ISSUER);
    assertThat(decodedJWT.getClaim("memberId").asLong()).isEqualTo(memberId);
  }

  @DisplayName("JWT 생성 실패할 경우 예외가 발생한다")
  @Test
  void throwAuthenticationExceptionIfJwtCreationFails() {
    var jwtProperties = mock(HmacJwtProperties.class);
    when(jwtProperties.getSecret()).thenReturn(MY_SECRET_KEY);
    when(jwtProperties.getAlgorithm()).thenReturn("INVALID-ALGORITHM");
    when(jwtProperties.getIssuer()).thenReturn(ISSUER);
    when(jwtProperties.getExpiration()).thenReturn(3600000L);

    assertThatThrownBy(() -> new HmacJavaJwtProvider(jwtProperties,  Clock.systemDefaultZone()))
        .isInstanceOf(IllegalArgumentException.class);
  }
}