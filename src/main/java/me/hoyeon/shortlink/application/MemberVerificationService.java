package me.hoyeon.shortlink.application;

import me.hoyeon.shortlink.domain.MemberRepository;
import me.hoyeon.shortlink.domain.VerifiedMember;

public class MemberVerificationService {

  private final MemberRepository memberRepository;
  private final EmailSender emailSender;

  public MemberVerificationService(MemberRepository memberRepository, EmailSender emailSender) {
    this.memberRepository = memberRepository;
    this.emailSender = emailSender;
  }

  public void sendVerificationMail(Long memberId) {
    var unverifiedMember = memberRepository.findUnverifiedById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(memberId));

    try {
      emailSender.send(
          unverifiedMember.getEmailString(),
          createVerificationSubject(),
          createVerificationContent(unverifiedMember.getTokenString())
      );
    } catch (EmailSendException e) {
      // 로깅, 연결 조정?
      throw e;
    }
  }

  // 런타임에 생성할 수 있는 이메일 템플릿 구현할 것
  private String createVerificationSubject() {
    return "Test Verification Subject";
  }

  private String createVerificationContent(String tokenValue) {
    return tokenValue;
  }

  public VerifiedMember verify(Long memberId, String verificationToken) {
    var unverifiedMember = memberRepository.findUnverifiedById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(memberId));

    var verifiedMember = unverifiedMember.verify(verificationToken);
    memberRepository.save(verifiedMember);
    return verifiedMember;
  }
}
