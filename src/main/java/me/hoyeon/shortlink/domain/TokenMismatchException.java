package me.hoyeon.shortlink.domain;

public class TokenMismatchException extends DomainException {

  public static final String MESSAGE = "주어진 토큰과 일치하지 않습니다";

  public TokenMismatchException(String token) {
    super(MESSAGE + " - " + token);
  }
}
