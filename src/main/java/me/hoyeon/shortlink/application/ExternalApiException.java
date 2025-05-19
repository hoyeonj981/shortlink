package me.hoyeon.shortlink.application;

public class ExternalApiException extends ApplicationException {

  public ExternalApiException(String message) {
    super(message);
  }

  public ExternalApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
