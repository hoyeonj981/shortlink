package me.hoyeon.shortlink.application;

public class UrlNotFoundException extends RuntimeException {

  private static final String MESSAGE = "등록된 URL을 찾을 수 없습니다.";

  public UrlNotFoundException(String value) {
    super(MESSAGE + " - " + value);
  }
}
