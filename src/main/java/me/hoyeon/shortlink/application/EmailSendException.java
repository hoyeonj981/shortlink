package me.hoyeon.shortlink.application;

public class EmailSendException extends RuntimeException {

  public static final String MESSAGE = "이메일 전송에 실패했습니다";

  public EmailSendException(String details, Throwable e) {
    super(MESSAGE + " - " + details, e);
  }
}
