package me.hoyeon.shortlink.domain;

public class InvalidEmailException extends DomainException {

  public static final String MESSAGE = "이메일 형식이 유효하지 않습니다";

  public InvalidEmailException(String email) {
    super(MESSAGE + " - " + email);
  }
}
