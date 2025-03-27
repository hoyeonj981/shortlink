package me.hoyeon.shortlink.application;

public class AuthenticationException extends RuntimeException {

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
