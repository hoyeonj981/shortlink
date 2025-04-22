package me.hoyeon.shortlink.application;

public class NotAccessibleUrlException extends ApplicationException {

  private static final String MESSAGE = "접근할 수 없는 URL입니다";

  public NotAccessibleUrlException(String value) {
    super(MESSAGE + " - " + value);
  }
}
