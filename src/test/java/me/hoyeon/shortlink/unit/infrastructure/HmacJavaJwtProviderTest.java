package me.hoyeon.shortlink.unit.infrastructure;

import static me.hoyeon.shortlink.domain.MemberVerificationStatus.UNVERIFIED;
import static me.hoyeon.shortlink.domain.MemberVerificationStatus.VERIFIED;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.MEMBER_ID;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.ROLE;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.TOKEN_TYPE;
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
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import me.hoyeon.shortlink.application.InvalidJwtTokenException;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.application.MemberQueryService;
import me.hoyeon.shortlink.domain.Member;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import me.hoyeon.shortlink.domain.VerifiedMember;
import me.hoyeon.shortlink.infrastructure.ClaimNotExistException;
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

  private MemberQueryService memberQueryService;

  @BeforeEach
  void setUp() {
    jwtProperties = new HmacJwtProperties();
    jwtProperties.setSecret(MY_SECRET_KEY);
    jwtProperties.setAlgorithm("HS256");
    jwtProperties.setIssuer(ISSUER);
    jwtProperties.setAccessExpiration(3600000L);
    jwtProperties.setRefreshExpiration(7200000L);

    jwtRepository = mock(JwtRepository.class);
    memberQueryService = mock(MemberQueryService.class);
    jwtProvider = new HmacJavaJwtProvider(
        jwtProperties,
        Clock.systemDefaultZone(),
        jwtRepository,
        memberQueryService);
  }

  @DisplayName("인증된 회원의 액세스 토큰을 생성한다")
  @Test
  void createAccessTokenForVerifiedMember() {
    var memberId = 1L;
    var verifiedMember = mock(VerifiedMember.class);
    when(verifiedMember.isVerified()).thenReturn(true);
    when(verifiedMember.getId()).thenReturn(memberId);

    var token = jwtProvider.generateAccessToken(verifiedMember);
    var decoded = JWT.decode(token);

    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
    assertThat(decoded.getId()).isNotNull();
    assertThat(decoded.getIssuedAt()).isNotNull();
    assertThat(decoded.getExpiresAt()).isNotNull();
    assertThat(decoded.getExpiresAt().after(decoded.getIssuedAt())).isTrue();
    assertThat(decoded.getClaim(MEMBER_ID.getClaimName()).asLong()).isEqualTo(memberId);
    assertThat(decoded.getClaim(ROLE.getClaimName()).asString()).isEqualTo(VERIFIED.getValue());
  }

  @DisplayName("미인증된 회원의 액세스 토큰을 생성한다")
  @Test
  void createAccessTokenForUnverifiedMember() {
    var memberId = 1L;
    var verifiedMember = mock(UnverifiedMember.class);
    when(verifiedMember.isVerified()).thenReturn(false);
    when(verifiedMember.getId()).thenReturn(memberId);

    var token = jwtProvider.generateAccessToken(verifiedMember);
    var decoded = JWT.decode(token);

    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
    assertThat(decoded.getId()).isNotNull();
    assertThat(decoded.getIssuedAt()).isNotNull();
    assertThat(decoded.getExpiresAt()).isNotNull();
    assertThat(decoded.getExpiresAt().after(decoded.getIssuedAt())).isTrue();
    assertThat(decoded.getClaim(MEMBER_ID.getClaimName()).asLong()).isEqualTo(memberId);
    assertThat(decoded.getClaim(ROLE.getClaimName()).asString()).isEqualTo(UNVERIFIED.getValue());
  }

  @DisplayName("회원의 리프레시 토큰을 생성한다")
  @Test
  void createRefreshTokenForMember() {
    var memberId = 1L;
    var member = mock(Member.class);
    when(member.getId()).thenReturn(memberId);

    var token = jwtProvider.generateRefreshToken(member);
    var decoded = JWT.decode(token);

    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
    assertThat(decoded.getId()).isNotNull();
    assertThat(decoded.getIssuedAt()).isNotNull();
    assertThat(decoded.getExpiresAt()).isNotNull();
    assertThat(decoded.getClaim(MEMBER_ID.getClaimName()).asLong()).isEqualTo(memberId);
    assertThat(decoded.getClaim(TOKEN_TYPE.getClaimName()).asString()).isEqualTo("refresh");
  }

  @DisplayName("액세스 토큰 생성 시 JWT 생성 실패할 경우 예외가 발생한다")
  @Test
  void throwAuthenticationExceptionIfJwtCreationFails() {
    var member = mock(VerifiedMember.class);
    jwtProperties.setSecret(EMPTY_SECRET);

    assertThatThrownBy(() -> jwtProvider.generateAccessToken(member))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("유효한 토큰을 검증한다")
  @Test
  void validateValidToken() {
    var member = mock(VerifiedMember.class);
    var validToken = jwtProvider.generateAccessToken(member);

    assertThatCode(() -> jwtProvider.validate(validToken))
        .doesNotThrowAnyException();
  }

  @DisplayName("잘못된 서명으로 서명된 토큰은 예외를 발생시킨다")
  @Test
  void throwExceptionForInvalidSignatureToken() {
    var tokenWithWrongSignature = JWT.create()
        .withIssuer(ISSUER)
        .withClaim(MEMBER_ID.getClaimName(), 1L)
        .sign(Algorithm.HMAC256("wrong-secret-key"));

    assertThatThrownBy(() -> jwtProvider.validate(tokenWithWrongSignature))
        .isInstanceOf(InvalidJwtTokenException.class);
  }

  @DisplayName("만료된 토큰은 예외를 발생시킨다")
  @Test
  void throwExceptionForExpiredToken() {
    var member = mock(VerifiedMember.class);
    jwtProperties.setAccessExpiration(-1L);

    var expiredToken = jwtProvider.generateAccessToken(member);

    assertThatThrownBy(() -> jwtProvider.validate(expiredToken))
        .isInstanceOf(InvalidJwtTokenException.class);
  }

  @DisplayName("유효한 리프레시 토큰을 생성한다")
  @Test
  void createValidRefreshToken() {
    var memberId = 1L;
    var member = mock(VerifiedMember.class);
    when(member.getId()).thenReturn(memberId);

    var refreshToken = jwtProvider.generateRefreshToken(member);
    var decodedJwt = JWT.require(Algorithm.HMAC256(MY_SECRET_KEY))
        .withIssuer(ISSUER)
        .build()
        .verify(refreshToken);

    assertThat(refreshToken).isNotNull();
    assertThat(decodedJwt.getIssuer()).isEqualTo(ISSUER);
    assertThat(decodedJwt.getClaim(MEMBER_ID.getClaimName()).asLong()).isEqualTo(memberId);
  }

  @DisplayName("리프레시 토큰 생성 시 JWT 생성에 실패할 경우 예외가 발생한다")
  @Test
  void throwAuthenticationExceptionIfJwtCreationFailsWhenCreatingRefreshToken() {
    var member = mock(VerifiedMember.class);
    jwtProperties.setSecret(EMPTY_SECRET);

    assertThatThrownBy(() -> jwtProvider.generateRefreshToken(member))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("유효한 리프레시 토큰으로 새로운 액세스 토큰을 발급한다")
  @Test
  void refreshAccessTokenWithValidRefreshToken() {
    var memberId = 1L;
    var member = mock(VerifiedMember.class);
    when(member.getId()).thenReturn(memberId);
    when(member.isVerified()).thenReturn(true);
    when(memberQueryService.getMemberById(memberId)).thenReturn(member);

    var refreshToken = jwtProvider.generateRefreshToken(member);
    var newAccessToken = jwtProvider.refreshAccessToken(refreshToken);
    var decoded = JWT.decode(newAccessToken);

    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
    assertThat(decoded.getClaim(MEMBER_ID.getClaimName()).asLong()).isEqualTo(memberId);
    assertThat(decoded.getClaim(ROLE.getClaimName()).asString()).isEqualTo(VERIFIED.getValue());
  }

  @DisplayName("유효하지 않은 리프레시 토큰으로 액세스 토큰 발급 시 예외가 발생한다")
  @Test
  void throwExceptionWhenRefreshingWithInvalidRefreshToken() {
    var invalidRefreshToken = "invalid-refresh-token";

    assertThatThrownBy(() -> jwtProvider.refreshAccessToken(invalidRefreshToken))
        .isInstanceOf(InvalidJwtTokenException.class);
  }

  @DisplayName("다른 발급자의 리프레시 토큰으로 액세스 토큰 발급 시 예외가 발생한다")
  @Test
  void throwExceptionWhenRefreshingWithDifferentIssuer() {
    var tokenWithDifferentIssuer = JWT.create()
        .withIssuer("different-issuer")
        .withClaim(MEMBER_ID.getClaimName(), 1L)
        .withClaim(TOKEN_TYPE.getClaimName(), "refresh")
        .sign(Algorithm.HMAC256(MY_SECRET_KEY));

    assertThatThrownBy(() -> jwtProvider.refreshAccessToken(tokenWithDifferentIssuer))
        .isInstanceOf(InvalidJwtTokenException.class);
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

  @DisplayName("토큰에서 유효한 클레임을 가져온다")
  @Test
  void getValidClaimFromToken() {
    var memberId = 1L;
    var member = mock(VerifiedMember.class);
    when(member.getId()).thenReturn(memberId);
    when(member.isVerified()).thenReturn(true);

    var token = jwtProvider.generateAccessToken(member);
    var claim = jwtProvider.getClaim(token, ROLE.getClaimName());

    assertThat(claim).isEqualTo(VERIFIED.getValue());
  }

  @DisplayName("존재하지 않는 클레임 키로 조회시 예외가 발생한다")
  @Test
  void throwExceptionWhenClaimKeyNotExists() {
    var member = mock(VerifiedMember.class);
    var token = jwtProvider.generateAccessToken(member);

    assertThatThrownBy(() -> jwtProvider.getClaim(token, "non-existing-key"))
        .isInstanceOf(ClaimNotExistException.class);
  }

  @DisplayName("유효하지 않은 토큰으로 클레임 조회시 예외가 발생한다")
  @Test
  void throwExceptionWhenTokenIsInvalid() {
    var invalidToken = "invalid-token";

    assertThatThrownBy(() -> jwtProvider.getClaim(invalidToken, ROLE.getClaimName()))
        .isInstanceOf(InvalidJwtTokenException.class);
  }

  private String createTestJwtToken(long expiresInSeconds) {
    var algorithm = Algorithm.HMAC256(MY_SECRET_KEY);
    var expiresAt = Date.from(Instant.now().plusSeconds(expiresInSeconds));

    return JWT.create()
        .withIssuer(ISSUER)
        .withExpiresAt(expiresAt)
        .sign(algorithm);
  }
}