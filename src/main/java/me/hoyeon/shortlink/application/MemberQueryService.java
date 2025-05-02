package me.hoyeon.shortlink.application;

import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.Member;
import me.hoyeon.shortlink.domain.MemberRepository;

public class MemberQueryService {

  private final MemberRepository memberRepository;
  private final EmailValidator emailValidator;

  public MemberQueryService(MemberRepository memberRepository, EmailValidator emailValidator) {
    this.memberRepository = memberRepository;
    this.emailValidator = emailValidator;
  }

  public Member getMemberByEmail(String emailAddress) {
    Email email = Email.from(emailAddress, emailValidator);
    return memberRepository.findByEmail(email)
        .orElseThrow(MemberNotFoundException::new);
  }
}