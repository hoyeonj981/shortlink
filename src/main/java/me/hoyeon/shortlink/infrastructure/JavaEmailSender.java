package me.hoyeon.shortlink.infrastructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import me.hoyeon.shortlink.application.EmailSendException;
import me.hoyeon.shortlink.application.EmailSender;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

public class JavaEmailSender implements EmailSender {

  private final JavaMailSender emailSender;
  private final EmailProperties emailProperties;
  private final RetryProperties retryProperties;
  private final EmailValidator emailValidator;

  public JavaEmailSender(
      JavaMailSender emailSender,
      EmailProperties emailProperties,
      RetryProperties retryProperties,
      EmailValidator emailValidator
  ) {
    this.emailSender = emailSender;
    this.emailProperties = emailProperties;
    this.retryProperties = retryProperties;
    this.emailValidator = emailValidator;
  }

  @Override
  public void send(String to, String subject, String content) throws EmailSendException {
    validateEmailAddress(to);

    var message = emailSender.createMimeMessage();
    var attempts = 1; // 반드시 1번은 실행됨
    var maxRetryAttempts = retryProperties.getMaxAttempts();

    try {
      message.setFrom(emailProperties.getFrom());
      message.setRecipients(MimeMessage.RecipientType.TO, to);
      message.setSubject(subject);
      message.setContent(content, "text/html; charset=utf-8");
      emailSender.send(message);
    } catch (MessagingException e) {
      throw new EmailSendException("이메일 메시지를 생성할 수 없습니다", e);
    } catch (MailSendException e) {
      // 메일 전송 실패
      // 재시도 로직 실행
      while (attempts < maxRetryAttempts) {
        try {
          emailSender.send(message);
          return;
        } catch (MailSendException ex) {
          attempts++;
        }
      }
      throw new EmailSendException("최대 이메일 전송 횟수 초과. 전송 실패 - " + attempts, e);
    } catch (MailAuthenticationException e) {
      // 메일 인증 실패
      throw new EmailSendException("이메일 서버 인증에 실패했습니다", e);
    } catch (MailException e) {
      // 기타 예외
      throw new EmailSendException("이메일 전송에 실패했습니다", e);
    }
  }

  private void validateEmailAddress(String to) {
    try {
      emailValidator.validate(to);
    } catch (InvalidEmailException e) {
      throw new EmailSendException("이메일 형식이 올바르지 않습니다.", e);
    }
  }
}
