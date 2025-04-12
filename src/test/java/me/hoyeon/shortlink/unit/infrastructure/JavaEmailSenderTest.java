package me.hoyeon.shortlink.unit.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import me.hoyeon.shortlink.application.EmailSendException;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import me.hoyeon.shortlink.infrastructure.EmailProperties;
import me.hoyeon.shortlink.infrastructure.JavaEmailSender;
import me.hoyeon.shortlink.infrastructure.RetryProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class JavaEmailSenderTest {

  private static final String FROM = "no-reply@example.com";
  private static final String TO = "test@example.com";
  private static final String SUBJECT = "Test Subject";
  private static final String CONTENT = "Test Text";

  @InjectMocks
  private JavaEmailSender emailSender;

  @Mock
  private JavaMailSender sender;

  @Mock
  private EmailProperties emailProperties;

  @Mock
  private RetryProperties retryProperties;

  @Mock
  private EmailValidator validator;

  @DisplayName("이메일 전송에 성공한다")
  @Test
  void sendEmailSuccessfully() throws Exception {
    var mockMessage = mock(MimeMessage.class);
    var givenRetryCount = 3;
    doNothing().when(validator).validate(TO);
    when(emailProperties.getFrom()).thenReturn(FROM);
    when(sender.createMimeMessage()).thenReturn(mockMessage);
    when(retryProperties.getMaxAttempts()).thenReturn(givenRetryCount);

    emailSender.send(TO, SUBJECT, CONTENT);

    verify(sender).send(mockMessage);
    verify(validator).validate(TO);
    verify(emailProperties).getFrom();
    verify(retryProperties).getMaxAttempts();
    verify(mockMessage).setFrom(anyString());
    verify(mockMessage).setRecipients(any(), eq(TO));
    verify(mockMessage).setSubject(anyString());
    verify(mockMessage).setContent(anyString(), anyString());
  }

  @DisplayName("잘못된 이메일 형식일 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenInvalidEmailFormat() {
    var givenInvalidEmail = "invalid-email";
    doThrow(InvalidEmailException.class).when(validator).validate(givenInvalidEmail);

    assertThatThrownBy(() -> emailSender.send(givenInvalidEmail, SUBJECT, CONTENT))
        .isInstanceOf(EmailSendException.class);
  }

  @DisplayName("이메일 메시지 생성할 수 없을 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenCouldNotCreateEmailMessage() throws Exception {
    var mockMessage = mock(MimeMessage.class);
    var givenRetryCount = 3;
    when(retryProperties.getMaxAttempts()).thenReturn(givenRetryCount);
    when(emailProperties.getFrom()).thenReturn(FROM);
    when(sender.createMimeMessage()).thenReturn(mockMessage);
    doThrow(MessagingException.class).when(mockMessage).setFrom(anyString());

    assertThatThrownBy(() -> emailSender.send(TO, SUBJECT, CONTENT))
        .isInstanceOf(EmailSendException.class);
  }

  @DisplayName("이메일 전송 실패시 최대 재시도 횟수만큼 시도한 후 예외가 발생한다")
  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 5})
  void throwExceptionAfterAllRetryAttemptsFailure(int givenRetryCount) {
    var mockMessage = mock(MimeMessage.class);
    when(retryProperties.getMaxAttempts()).thenReturn(givenRetryCount);
    when(emailProperties.getFrom()).thenReturn(FROM);
    when(sender.createMimeMessage()).thenReturn(mockMessage);
    doThrow(MailSendException.class).when(sender).send(mockMessage);

    assertThatThrownBy(() -> emailSender.send(TO, SUBJECT, CONTENT))
        .isInstanceOf(EmailSendException.class);
    verify(sender, times(givenRetryCount)).send(mockMessage);
  }

  @DisplayName("재시도 횟수 3번 중 2번 시도하여 이메일 전송에 성공한다")
  @Test
  void retryAndSuccessSendingEmail() {
    var mockMessage = mock(MimeMessage.class);
    var retry3Times = 3;
    when(retryProperties.getMaxAttempts()).thenReturn(retry3Times);
    when(emailProperties.getFrom()).thenReturn(FROM);
    when(sender.createMimeMessage()).thenReturn(mockMessage);
    doThrow(MailSendException.class)
        .doNothing()
        .when(sender).send(mockMessage);

    emailSender.send(TO, SUBJECT, CONTENT);

    verify(sender, times(2)).send(mockMessage);
  }

  @DisplayName("이메일 서버 인증에 실패할 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenFailedToAuthenticateEmailServer() {
    var mockMessage = mock(MimeMessage.class);
    var givenRetryCount = 3;
    doNothing().when(validator).validate(TO);
    when(emailProperties.getFrom()).thenReturn(FROM);
    when(sender.createMimeMessage()).thenReturn(mockMessage);
    when(retryProperties.getMaxAttempts()).thenReturn(givenRetryCount);
    doThrow(MailAuthenticationException.class).when(sender).send(mockMessage);

    assertThatThrownBy(() -> emailSender.send(TO, SUBJECT, CONTENT))
        .isInstanceOf(EmailSendException.class);
  }

  @DisplayName("이메일 전송이 불가능할 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenCouldNotSendEmail() {
    var mockMessage = mock(MimeMessage.class);
    var givenRetryCount = 3;
    doNothing().when(validator).validate(TO);
    when(emailProperties.getFrom()).thenReturn(FROM);
    when(sender.createMimeMessage()).thenReturn(mockMessage);
    when(retryProperties.getMaxAttempts()).thenReturn(givenRetryCount);
    doThrow(MailPreparationException.class).when(sender).send(mockMessage);

    assertThatThrownBy(() -> emailSender.send(TO, SUBJECT, CONTENT))
        .isInstanceOf(EmailSendException.class);
  }
}
