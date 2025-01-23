package me.hoyeon.shortlink.domain;

import java.net.MalformedURLException;

public class InvalidUrlException extends RuntimeException {

  private static final String MESSAGE = "유효하지 않는 URL입니다.";

  public InvalidUrlException(String value) {
    super(MESSAGE + " - " + value);
  }

  public InvalidUrlException(String value, MalformedURLException e) {
    super(MESSAGE + " - " + value, e);
  }
}
