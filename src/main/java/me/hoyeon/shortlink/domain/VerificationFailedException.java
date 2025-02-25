package me.hoyeon.shortlink.domain;

public class VerificationFailedException extends RuntimeException {


  public VerificationFailedException(String message, Throwable e) {
    super(message, e);
  }
}
