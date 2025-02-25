package me.hoyeon.shortlink.domain;

public class TokenExpiredException extends RuntimeException {

  public static final String MESSAGE = "토큰 시간이 만료되었습니다";

  public TokenExpiredException() {
    super(MESSAGE);
  }
}
