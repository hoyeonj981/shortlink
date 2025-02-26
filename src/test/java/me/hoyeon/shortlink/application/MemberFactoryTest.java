package me.hoyeon.shortlink.application;

import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import me.hoyeon.shortlink.domain.InvalidPasswordException;
import me.hoyeon.shortlink.domain.PasswordEncoder;
import me.hoyeon.shortlink.domain.PasswordValidator;
import me.hoyeon.shortlink.domain.VerificationToken;
import me.hoyeon.shortlink.domain.VerificationTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberFactoryTest {

  private static final Instant STANDARD_TIME = Instant.parse("2024-02-02T10:00:00Z");
  private static final Clock STANDARD_CLOCK = Clock.fixed(STANDARD_TIME, systemDefault());

  private static final Long MEMBER_ID = 1L;
  private static final String ENCRYPTED_PASSWORD = "asdfasd324#@";
  private static final String VALID_EMAIL = "test@example.com";
  private static final String INVALID_EMAIL = "invalid-email";
  private static final String VALID_PASSWORD = "abcdef123!";
  private static final String INVALID_PASSWORD = "invalid-password";

  @Mock
  private SimpleIdGenerator idGenerator;

  @Mock
  private VerificationTokenGenerator tokenGenerator;

  @Mock
  private EmailValidator emailValidator;

  @Mock
  private PasswordValidator passwordValidator;

  @Mock
  private PasswordEncoder passwordEncoder;

  private MemberFactory memberFactory;

  @BeforeEach
  void setUp() {
    memberFactory = new MemberFactory(
        idGenerator,
        tokenGenerator,
        emailValidator,
        passwordValidator,
        passwordEncoder,
        STANDARD_CLOCK);

  }

  @DisplayName("유효한 이메일과 비밀번호로 UnverifiedMember를 생성한다")
  @Test
  void shouldCreateUnverifiedMemberWithValidEmailAndPassword() {
    var givenEmail = Email.of(VALID_EMAIL);
    var givenVerificationToken = mock(VerificationToken.class);
    doNothing().when(emailValidator).validate(VALID_EMAIL);
    doNothing().when(passwordValidator).validate(VALID_PASSWORD);
    when(passwordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
    when(idGenerator.getId()).thenReturn(MEMBER_ID);
    when(tokenGenerator.generate(givenEmail, STANDARD_CLOCK)).thenReturn(givenVerificationToken);

    var unverifiedMember = memberFactory.createNew(VALID_EMAIL, VALID_PASSWORD);

    assertThat(unverifiedMember).isNotNull();
    verify(emailValidator).validate(VALID_EMAIL);
    verify(passwordValidator).validate(VALID_PASSWORD);
    verify(passwordEncoder).encode(VALID_PASSWORD);
    verify(idGenerator).getId();
    verify(tokenGenerator).generate(givenEmail, STANDARD_CLOCK);
  }

  @DisplayName("잘못된 이메일로 생성 시 예외가 발생한다")
  @Test
  void shouldThrowExceptionWithInvalidEmail() {
    doThrow(InvalidEmailException.class).when(emailValidator).validate(INVALID_EMAIL);

    assertThatThrownBy(() -> memberFactory.createNew(INVALID_EMAIL, VALID_PASSWORD))
        .isInstanceOf(InvalidEmailException.class);
  }

  @DisplayName("잘못된 비밀번호로 생성 시 예외가 발생한다")
  @Test
  void shouldThrowExceptionWithInvalidPassword() {
    doThrow(InvalidPasswordException.class).when(passwordValidator).validate(INVALID_PASSWORD);

    assertThatThrownBy(() -> memberFactory.createNew(VALID_EMAIL, INVALID_PASSWORD))
        .isInstanceOf(InvalidPasswordException.class);
  }
}
