package me.hoyeon.shortlink.application;

public class MismatchPasswordException extends RuntimeException {

  public static final String MESSAGE = "패스워드가 일치하지 않습니다";

  public MismatchPasswordException() {
    super(MESSAGE);
  }
}
