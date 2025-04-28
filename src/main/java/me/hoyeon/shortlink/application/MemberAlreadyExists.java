package me.hoyeon.shortlink.application;

public class MemberAlreadyExists extends ApplicationException {

  public static final String MESSAGE = "해당 이메일로 가입한 회원이 존재합니다";

  public MemberAlreadyExists(String emailAddress) {
    super(MESSAGE + " - " + emailAddress);
  }
}
