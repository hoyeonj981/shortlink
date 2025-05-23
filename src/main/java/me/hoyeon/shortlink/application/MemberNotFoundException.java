package me.hoyeon.shortlink.application;

public class MemberNotFoundException extends ApplicationException {

  public static final String MESSAGE = "존재하지 않는 회원입니다";

  public MemberNotFoundException() {
    super(MESSAGE);
  }

  public MemberNotFoundException(long memberId) {
    super(MESSAGE + " - " + memberId);
  }
}
