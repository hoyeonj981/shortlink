package me.hoyeon.shortlink.domain;

public class InvalidExpirationTimeException extends DomainException {

  private static final String MESSAGE = "만료기간이 유효하지 않습니다.";

  public InvalidExpirationTimeException() {
    super(MESSAGE);
  }
}
