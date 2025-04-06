package me.hoyeon.shortlink.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import me.hoyeon.shortlink.domain.MemberRepository;
import me.hoyeon.shortlink.domain.PasswordEncoder;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password";
  private static final String ACCESS_TOKEN = "access-token";
  private static final String REFRESH_TOKEN = "refresh-token";

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private EmailValidator emailValidator;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @InjectMocks
  private AuthenticationService authenticationService;

  @DisplayName("로그인에 성공하면 엑세스 토큰을 반환한다")
  @Test
  void signInSuccessfully() {
    var member = mock(UnverifiedMember.class);
    when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
    doNothing().when(emailValidator).validate(anyString());
    when(member.matchPassword(anyString(), eq(passwordEncoder))).thenReturn(true);
    when(jwtTokenProvider.generateAccessToken(anyLong())).thenReturn(ACCESS_TOKEN);
    when(jwtTokenProvider.generateRefreshToken(anyLong())).thenReturn(REFRESH_TOKEN);

    var result = authenticationService.signIn(TEST_EMAIL, TEST_PASSWORD);

    assertThat(result).isNotNull();
    assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
    assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
  }

  @DisplayName("이메일 형식이 올바르지 않는다면 예외가 발생한다")
  @Test
  void throwExceptionWhenEmailIsInvalid() {
    doThrow(InvalidEmailException.class).when(emailValidator).validate(anyString());

    assertThatThrownBy(() -> authenticationService.signIn(TEST_EMAIL, TEST_PASSWORD))
        .isInstanceOf(AuthenticationException.class);
  }

  @DisplayName("이메일로 회원을 찾을 수 없다면 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberNotFound() {
    doNothing().when(emailValidator).validate(anyString());
    when(memberRepository.findByEmail(any())).thenThrow(MemberNotFoundException.class);

    assertThatThrownBy(() -> authenticationService.signIn(TEST_EMAIL, TEST_PASSWORD))
        .isInstanceOf(MemberNotFoundException.class);
  }

  @DisplayName("로그인 시 비밀번호가 틀리다면 예외가 발생한다")
  @Test
  void throwExceptionWhenPasswordIsWrong() {
    var member = mock(UnverifiedMember.class);
    doNothing().when(emailValidator).validate(anyString());
    when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
    when(member.matchPassword(anyString(), eq(passwordEncoder))).thenReturn(false);

    assertThatThrownBy(() -> authenticationService.signIn(TEST_EMAIL, TEST_PASSWORD))
        .isInstanceOf(MismatchPasswordException.class);
  }

  @DisplayName("로그아웃에 성공하면 토큰이 무효화된다")
  @Test
  void invalidateAccessTokenSuccessfullyWhenSignOut() {
    doNothing().when(jwtTokenProvider).validate(anyString());
    doNothing().when(jwtTokenProvider).invalidate(anyString());

    authenticationService.signOut(ACCESS_TOKEN);

    verify(jwtTokenProvider).invalidate(ACCESS_TOKEN);
  }

  @DisplayName("로그아웃할 토큰이 유효하지 않다면 예외가 발생한다")
  @Test
  void throwExceptionWhenAccessTokenIsInvalidWhenSignOut() {
    doThrow(InvalidJwtTokenException.class).when(jwtTokenProvider).validate(anyString());

    assertThatThrownBy(() -> authenticationService.signOut(ACCESS_TOKEN))
        .isInstanceOf(InvalidJwtTokenException.class);
  }
}