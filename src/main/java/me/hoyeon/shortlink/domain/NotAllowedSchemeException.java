package me.hoyeon.shortlink.domain;

public class NotAllowedSchemeException extends DomainException {

  private static final String MESSAGE = "HTTP(S) 스키마만 허용합니다";

  public NotAllowedSchemeException(String value) {
    super(MESSAGE + " - " + value);
  }
}
