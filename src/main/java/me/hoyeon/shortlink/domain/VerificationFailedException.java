package me.hoyeon.shortlink.domain;

public class VerificationFailedException extends DomainException {

  public VerificationFailedException(String message, Throwable e) {
    super(message, e);
  }
}
