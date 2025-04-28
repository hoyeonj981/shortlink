package me.hoyeon.shortlink.application;

import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.MemberRepository;

public class MemberRegistrationService {

  private final MemberRepository memberRepository;
  private final MemberFactory memberFactory;
  private final MemberVerificationService memberVerificationService;
  private final OauthCredentialRepository oauthCredentialRepository;

  public MemberRegistrationService(
      MemberRepository memberRepository,
      MemberFactory memberFactory,
      MemberVerificationService memberVerificationService,
      OauthCredentialRepository oauthCredentialRepository
  ) {
    this.memberRepository = memberRepository;
    this.memberFactory = memberFactory;
    this.memberVerificationService = memberVerificationService;
    this.oauthCredentialRepository = oauthCredentialRepository;
  }

  public void registerWithEmail(String emailAddress, String rawPassword) {
    var email = Email.of(emailAddress);
    validateMemberExists(email);

    var unverifiedMember = memberFactory.createNew(emailAddress, rawPassword);
    memberRepository.save(unverifiedMember);
    memberVerificationService.sendVerificationMail(unverifiedMember.getId());
  }

  public void registerWithOauth(OauthInfo info) {
    var email = Email.of(info.email());
    validateMemberExists(email);

    var verifiedMember = memberFactory.createNewWithOauth(info.email());
    memberRepository.save(verifiedMember);
    oauthCredentialRepository.save(verifiedMember.getId(), info.email(), info.provider());
  }

  private void validateMemberExists(Email email) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberAlreadyExists(email.getAddress());
    }
  }
}
