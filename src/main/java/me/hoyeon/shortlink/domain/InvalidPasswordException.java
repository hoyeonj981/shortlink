package me.hoyeon.shortlink.domain;

public class InvalidPasswordException extends DomainException {

  public static final String MESSAGE = "비밀번호 형식이 올바르지 않습니다";

  public InvalidPasswordException(String value) {
    super(MESSAGE + " - " + value);
  }
}
