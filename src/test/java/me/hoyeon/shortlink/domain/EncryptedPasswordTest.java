package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EncryptedPasswordTest {

  @DisplayName("비밀번호 값을 암호화하여 객체를 생성한다")
  @Test
  void shouldEncryptPasswordAndCreatePasswordObject() {
    var givenPassword = "randompassword";
    var encodedPassword= "encodedpassword";
    var mockEncoder = mock(PasswordEncoder.class);
    when(mockEncoder.encode(anyString())).thenReturn(encodedPassword);

    var encryptedPassword = EncryptedPassword.create(givenPassword, mockEncoder);

    assertThat(encryptedPassword).isNotNull();
    verify(mockEncoder).encode(givenPassword);
  }

  @DisplayName("비밀번호 값이 같다면 matches는 true을 반환한다")
  @Test
  void methodMatchesShouldReturnTrueIfPasswordIsCorrect() {
    var givenPassword = "randompassword";
    var encodedPassword= "encodedpassword";
    var mockEncoder = mock(PasswordEncoder.class);
    when(mockEncoder.encode(anyString())).thenReturn(encodedPassword);
    when(mockEncoder.matches(anyString(), anyString())).thenReturn(true);

    var encryptedPassword = EncryptedPassword.create(givenPassword, mockEncoder);

    assertThat(encryptedPassword.matches(givenPassword, mockEncoder)).isTrue();
  }

  @DisplayName("비밀번호 값이 다르다면 matches는 false를 반환한다")
  @Test
  void methodMatchesShouldReturnFalseIfPasswordIsNotCorrect() {
    var givenPassword = "randompassword";
    var encodedPassword= "encodedpassword";
    var mockEncoder = mock(PasswordEncoder.class);
    when(mockEncoder.encode(anyString())).thenReturn(encodedPassword);
    when(mockEncoder.matches(anyString(), anyString())).thenReturn(false);

    var encryptedPassword = EncryptedPassword.create(givenPassword, mockEncoder);

    assertThat(encryptedPassword.matches(givenPassword, mockEncoder)).isFalse();
  }
}
