package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VerifiedMemberTest {

  private PasswordEncoder encoder;

  @BeforeEach
  void setUp() {
    encoder = mock(PasswordEncoder.class);
  }

  @DisplayName("주어진 비밀번호가 사용자의 것과 같다면 true를 반환한다")
  @Test
  void returnTrueIfGivenPasswordMatchesCurrentPassword() {
    var givenId = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var password = "abcde1234!";
    var givenPassword = EncryptedPassword.create(password, encoder);
    var member = VerifiedMember.create(givenId, givenEmail, givenPassword);
    when(encoder.matches(any(), any())).thenReturn(true);

    var actual = member.matchPassword(password, encoder);

    assertThat(actual).isTrue();
  }

  @DisplayName("주어진 비밀번호가 사용자의 것과 다르다면 false를 반환한다")
  @Test
  void returnFalseIfGivenPasswordDoesNotMatchCurrentPassword() {
    var givenId = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var password = "abcde1234!";
    var givenPassword = EncryptedPassword.create(password, encoder);
    var member = VerifiedMember.create(givenId, givenEmail, givenPassword);
    when(encoder.matches(any(), any())).thenReturn(false);

    var actual = member.matchPassword(password, encoder);

    assertThat(actual).isFalse();
  }

  @DisplayName("기존 비밀번호와 변경할 비밀번호가 동일할 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenNewPasswordIsSameAsCurrentPassword() {
    var givenId = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var currentPassword = "abcde1234!";
    var newPassword = currentPassword;
    var givenPassword = EncryptedPassword.create(currentPassword, encoder);
    var member = VerifiedMember.create(givenId, givenEmail, givenPassword);

    assertThatThrownBy(() -> member.changePassword(currentPassword, newPassword, encoder))
        .isInstanceOf(SamePasswordException.class);
  }

  @DisplayName("비밀번호 변경 시 기존 비밀번호와 주어진 비밀번호가 다르다면 예외가 발생한다")
  @Test
  void throwExceptionIfGivenPasswordDoesNotMatchCurrentPasswordWhileChangingPassword() {
    var givenId = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var currentPassword = "abcde1234!";
    var notCurrentPassword = "234Dfsde!";
    var newPassword = "ABCDE4567!";
    var givenPassword = EncryptedPassword.create(currentPassword, encoder);
    var member = VerifiedMember.create(givenId, givenEmail, givenPassword);
    when(encoder.matches(any(), any())).thenReturn(false);

    assertThatThrownBy(() -> member.changePassword(notCurrentPassword, newPassword, encoder))
        .isInstanceOf(NotCorrectPasswordException.class);
  }

  @DisplayName("기존 비밀번호를 새로운 비밀번호로 변환한다")
  @Test
  void changePasswordSuccessfully() {
    var givenId = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var currentPassword = "abcde1234!";
    var newPassword = "ABCDE4567!";
    var givenPassword1 = EncryptedPassword.create(currentPassword, encoder);
    var member = VerifiedMember.create(givenId, givenEmail, givenPassword1);
    when(encoder.matches(any(), any())).thenReturn(true);

   assertThatCode(() ->member.changePassword(currentPassword, newPassword, encoder))
       .doesNotThrowAnyException();
  }

  @DisplayName("id값이 동일하다면 같은 객체이다")
  @Test
  void sameObjectIfIdValueIsSame() {
    var givenId1 = 1L;
    var givenId2 = 1L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var password1 = "abcde1234!";
    var givenPassword1 = EncryptedPassword.create(password1, encoder);
    var givenPassword2 = EncryptedPassword.create(password1, encoder);
    var member1 = VerifiedMember.create(givenId1, givenEmail, givenPassword1);
    var member2 = VerifiedMember.create(givenId2, givenEmail, givenPassword2);

    assertThat(givenId1).isEqualTo(givenId2);
    assertThat(member1).isEqualTo(member2);
  }

  @DisplayName("id값이 다르다면 다른 객체이다")
  @Test
  void differentObjectIfIdValueIsNotSame() {
    var givenId1 = 1L;
    var givenId2 = 2L;
    var emailAddress = "test@example.com";
    var givenEmail = Email.of(emailAddress);
    var password1 = "abcde1234!";
    var givenPassword1 = EncryptedPassword.create(password1, encoder);
    var givenPassword2 = EncryptedPassword.create(password1, encoder);
    var member1 = VerifiedMember.create(givenId1, givenEmail, givenPassword1);
    var member2 = VerifiedMember.create(givenId2, givenEmail, givenPassword2);

    assertThat(givenId1).isNotEqualTo(givenId2);
    assertThat(member1).isNotEqualTo(member2);
  }
}