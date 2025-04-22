package me.hoyeon.shortlink.application;

public class InvalidJwtTokenException extends ApplicationException {

  public static final String MESSAGE = "토큰 값이 유효하지 않습니다";

  public InvalidJwtTokenException() {
    super(MESSAGE);
  }

  public InvalidJwtTokenException(String message, Throwable e) {
    super(message, e);
  }
}
