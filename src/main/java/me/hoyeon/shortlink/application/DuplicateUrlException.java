package me.hoyeon.shortlink.application;

public class DuplicateUrlException extends ApplicationException {

  private static final String MESSAGE = "이미 존재하는 url입니다";

  public DuplicateUrlException() {
    super(MESSAGE);
  }

  public DuplicateUrlException(String value) {
    super(MESSAGE + " - " + value);
  }
}
