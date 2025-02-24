package me.hoyeon.shortlink.domain;

public class NotCorrectPasswordException extends RuntimeException {

  public static final String MESSAGE = "비밀번호가 일치하지 않습니다";

  public NotCorrectPasswordException() {
    super(MESSAGE);
  }
}
