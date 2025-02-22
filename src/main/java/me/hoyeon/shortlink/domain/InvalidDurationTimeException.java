package me.hoyeon.shortlink.domain;

public class InvalidDurationTimeException extends RuntimeException {

  private static final String MESSAGE = "유효하지 않는 시간 기간입니다";

  public InvalidDurationTimeException() {
    super(MESSAGE);
  }


  public InvalidDurationTimeException(String time) {
    super(MESSAGE + " - " + time);
  }
}
