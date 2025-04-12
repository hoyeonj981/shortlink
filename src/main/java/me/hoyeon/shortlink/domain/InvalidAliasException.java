package me.hoyeon.shortlink.domain;

public class InvalidAliasException extends DomainException {

  private static final String MESSAGE = "유효하지 않는 별명입니다.";

  public InvalidAliasException(String value) {
    super(MESSAGE + " -" + value);
  }

  public InvalidAliasException() {
    super(MESSAGE);
  }
}
