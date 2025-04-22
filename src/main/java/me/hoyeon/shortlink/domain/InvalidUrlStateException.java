package me.hoyeon.shortlink.domain;

public class InvalidUrlStateException extends DomainException {

  private static final String MESSAGE = "현재 사용할 수 없는 단축 url 입니다";

  public InvalidUrlStateException() {
    super(MESSAGE);
  }
}
