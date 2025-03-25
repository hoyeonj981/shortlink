package me.hoyeon.shortlink.application;

public class InvalidJwtTokenException extends RuntimeException {

  public static final String MESSAGE = "토큰 값이 유효하지 않습니다";

  public InvalidJwtTokenException() {
    super(MESSAGE);
  }
}
