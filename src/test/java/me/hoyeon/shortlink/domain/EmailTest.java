package me.hoyeon.shortlink.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

  @DisplayName("이메일로 전달된 username과 domain은 항상 소문자로 처리한다")
  @ParameterizedTest
  @ValueSource(strings = {
      "TESTER@example.com",
      "tester@EXAMPLE.COM",
      "TesTER@example.com",
      "tester@EXamPle.Com"
  })
  void givenEmailStringShouldBeLowercase(String givenEmail) {
    var expectedEmail = givenEmail.toLowerCase();

    var email = Email.of(givenEmail);
    var actual = email.getAddress();

    assertThat(actual).isEqualTo(expectedEmail);
  }

  @DisplayName("이메일 주소가 같으면 같은 객체이다")
  @Test
  void sameObjectIfEmailAddressIsSame() {
    var givenEmail1 = "tester@example.com";
    var givenEmail2 = "tester@example.com";

    var email1 = Email.of(givenEmail1);
    var email2 = Email.of(givenEmail2);

    assertThat(givenEmail1).isEqualTo(givenEmail2);
    assertThat(email1).isEqualTo(email2);
  }

  @DisplayName("이메일 주소가 다르면 다른 객체이다")
  @Test
  void differentObjectIfEmailAddressIsNotSame() {
    var givenEmail1 = "tester@example.com";
    var givenEmail2 = "example@example.com";

    var email1 = Email.of(givenEmail1);
    var email2 = Email.of(givenEmail2);

    assertThat(givenEmail1).isNotEqualTo(givenEmail2);
    assertThat(email1).isNotEqualTo(email2);
  }
}
