package me.hoyeon.shortlink.domain;

public class InvalidUrlIdException extends DomainException {

  private static final String MESSAGE = "유효하지 않는 Id값 입니다.";

  public InvalidUrlIdException(Long value) {
    super(MESSAGE + " - " + value);
  }
}
