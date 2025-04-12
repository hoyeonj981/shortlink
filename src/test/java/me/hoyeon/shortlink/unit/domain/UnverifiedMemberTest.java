package me.hoyeon.shortlink.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EncryptedPassword;
import me.hoyeon.shortlink.domain.PasswordEncoder;
import me.hoyeon.shortlink.domain.TokenExpiredException;
import me.hoyeon.shortlink.domain.TokenMismatchException;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import me.hoyeon.shortlink.domain.VerificationFailedException;
import me.hoyeon.shortlink.domain.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnverifiedMemberTest {

  private PasswordEncoder encoder;

  private VerificationToken token;

  @BeforeEach
  void setUp() {
    encoder = mock(PasswordEncoder.class);
    token = mock(VerificationToken.class);
  }

  @DisplayName("주어진 토큰이 일치하지 않는다면 예외가 발생한다")
  @Test
  void throwExceptionIfGivenTokenDoesNotMatch() {
    var givenId = 1L;
    var givenToken = "abcdef";
    var givenEmail = Email.of("test@example.com");
    var givenPassword = EncryptedPassword.create("abcde123!", encoder);
    doThrow(TokenMismatchException.class).when(token).verify(anyString());

    var member = UnverifiedMember.create(givenId, givenEmail, givenPassword, token);

    assertThatThrownBy(() -> member.verify(givenToken))
        .isInstanceOf(VerificationFailedException.class);
  }

  @DisplayName("주어진 토큰이 만료되었다면 예외가 발생한다")
  @Test
  void throwExceptionIfTokenIsExpired() {
    var givenId = 1L;
    var givenToken = "abcdef";
    var givenEmail = Email.of("test@example.com");
    var givenPassword = EncryptedPassword.create("abcde123!", encoder);
    doThrow(TokenExpiredException.class).when(token).verify(anyString());

    var member = UnverifiedMember.create(givenId, givenEmail, givenPassword, token);

    assertThatThrownBy(() -> member.verify(givenToken))
        .isInstanceOf(VerificationFailedException.class);
  }

  @DisplayName("주어진 토큰이 일치하고 시간이 유효하다면 VerificationMember를 생성한다")
  @Test
  void createVerifiedMemberWhenTokenMatchesAndIsNotExpired() {
    var givenId = 1L;
    var givenToken = "abcdef";
    var givenEmail = Email.of("test@example.com");
    var givenPassword = EncryptedPassword.create("abcde123!", encoder);
    var member = UnverifiedMember.create(givenId, givenEmail, givenPassword, token);

    assertThatCode(() -> member.verify(givenToken))
        .doesNotThrowAnyException();
  }

  @DisplayName("주어진 비밀번호가 사용자의 것과 같다면 true를 반환한다")
  @Test
  void returnTrueIfGivenPasswordMatchesCurrentPassword() {
    var givenId = 1L;
    var givenEmail = Email.of("test@example.com");
    var password = "abcde1234!";
    var givenPassword = EncryptedPassword.create(password, encoder);
    var member = UnverifiedMember.create(givenId, givenEmail, givenPassword, token);
    when(encoder.matches(any(), any())).thenReturn(true);

    var actual = member.matchPassword(password, encoder);

    assertThat(actual).isTrue();
  }

  @DisplayName("주어진 비밀번호가 사용자의 것과 다르다면 false를 반환한다")
  @Test
  void returnFalseIfGivenPasswordDoesNotMatchCurrentPassword() {
    var givenId = 1L;
    var givenEmail = Email.of("test@example.com");
    var password = "abcde1234!";
    var givenPassword = EncryptedPassword.create(password, encoder);
    var member = UnverifiedMember.create(givenId, givenEmail, givenPassword, token);
    when(encoder.matches(any(), any())).thenReturn(false);

    var actual = member.matchPassword(password, encoder);

    assertThat(actual).isFalse();
  }

  @DisplayName("id값이 같다면 같은 객체이다")
  @Test
  void sameObjectIfIdValueIsSame() {
    var givenId1 = 1L;
    var givenId2 = 1L;
    var givenEmail = Email.of("test@example.com");
    var givenPassword = EncryptedPassword.create("abcde123!", encoder);
    var member1 = UnverifiedMember.create(givenId1, givenEmail, givenPassword, token);
    var member2 = UnverifiedMember.create(givenId2, givenEmail, givenPassword, token);

    assertThat(givenId1).isEqualTo(givenId2);
    assertThat(member1).isEqualTo(member2);
  }

  @DisplayName("id값이 다르다면 다른 객체이다")
  @Test
  void differentObjectIfIdValueIsNotSame() {
    var givenId1 = 1L;
    var givenId2 = 2L;
    var givenEmail = Email.of("test@example.com");
    var givenPassword = EncryptedPassword.create("abcde123!", encoder);
    var member1 = UnverifiedMember.create(givenId1, givenEmail, givenPassword, token);
    var member2 = UnverifiedMember.create(givenId2, givenEmail, givenPassword, token);

    assertThat(givenId1).isNotEqualTo(givenId2);
    assertThat(member1).isNotEqualTo(member2);
  }

  @DisplayName("미인증된 회원은 isVerified 메서드에서 항상 거짓 값을 반환한다")
  @Test
  void unVerifiedMemberShouldReturnFalseForIsVerifiedMethod() {
    var givenId = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var password1 = "abcde1234!";
    var givenPassword1 = EncryptedPassword.create(password1, encoder);
    var member = UnverifiedMember.create(givenId, givenEmail, givenPassword1, token);

    assertThat(member.isVerified()).isFalse();
  }
}
