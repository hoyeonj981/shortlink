package me.hoyeon.shortlink.domain;

public class SamePasswordException extends DomainException {

  public static final String MESSAGE = "현재 비밀번호와 변경 비밀번호가 동일합니다";

  public SamePasswordException() {
    super(MESSAGE);
  }
}
