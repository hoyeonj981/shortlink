package me.hoyeon.shortlink.domain;

public class NotAllowedSchemeException extends RuntimeException {

  private static final String MESSAGE = "HTTP(S) 스키마만 허용합니다";

  public NotAllowedSchemeException(String value) {
    super(MESSAGE + " - " + value);
  }
}
