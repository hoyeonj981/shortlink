package me.hoyeon.shortlink.unit.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import me.hoyeon.shortlink.application.MemberAlreadyExists;
import me.hoyeon.shortlink.application.MemberFactory;
import me.hoyeon.shortlink.application.MemberRegistrationService;
import me.hoyeon.shortlink.application.MemberVerificationService;
import me.hoyeon.shortlink.application.OauthCredentialRepository;
import me.hoyeon.shortlink.application.OauthInfo;
import me.hoyeon.shortlink.domain.MemberRepository;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import me.hoyeon.shortlink.domain.VerifiedMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberRegistrationServiceTest {

  @Mock
  private MemberFactory memberFactory;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberVerificationService memberVerificationService;

  @Mock
  private OauthCredentialRepository oauthCredentialRepository;

  @InjectMocks
  private MemberRegistrationService memberRegistrationService;

  @DisplayName("이메일로 회원가입 시 새로운 미회원을 생성하고 인증 이메일을 전송한다")
  @Test
  void createNewMemberAndSendVerificationEmailSuccessfully() {
    var memberId = 1L;
    var unverifiedMember = mock(UnverifiedMember.class);
    when(unverifiedMember.getId()).thenReturn(memberId);
    when(memberFactory.createNew(any(), any())).thenReturn(unverifiedMember);
    when(memberRepository.existsByEmail(any())).thenReturn(false);
    var emailAddress = "test@example.com";
    var rawPassword = "test-password";

    memberRegistrationService.registerWithEmail(emailAddress, rawPassword);

    verify(memberRepository).save(unverifiedMember);
    verify(memberVerificationService).sendVerificationMail(memberId);
  }

  @DisplayName("이메일로 회원가입 시 이미 존재하는 이메일이면 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberAlreadyExistsDuringRegisteringWithEmailUsingEmailAddress() {
    var emailAddress = "test@example.com";
    var rawPassword = "test-password";
    when(memberRepository.existsByEmail(any())).thenReturn(true);

    assertThatThrownBy(() -> memberRegistrationService.registerWithEmail(emailAddress, rawPassword))
        .isInstanceOf(MemberAlreadyExists.class);
  }

  @DisplayName("OAuth로 회원가입 시 새로운 인증회원을 생성하고 OAuth 정보를 저장한다")
  @Test
  void createNewMemberAndSaveOauthInfoSuccessfully() {
    var memberId = 1L;
    var verifiedMember = mock(VerifiedMember.class);
    when(memberFactory.createNewWithOauth(any())).thenReturn(verifiedMember);
    when(memberRepository.existsByEmail(any())).thenReturn(false);
    var emailAddress = "test@example.com";
    var provider = "google";
    var oauthInfo = new OauthInfo(emailAddress, provider);
    when(verifiedMember.getId()).thenReturn(memberId);

    memberRegistrationService.registerWithOauth(oauthInfo);

    verify(memberRepository).save(verifiedMember);
    verify(oauthCredentialRepository).save(memberId, emailAddress, provider);
  }

  @DisplayName("OAuth로 회원가입 시 이미 존재하는 이메일이면 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberAlreadyExistsDuringRegisteringWithOauthUsingEmailAddress() {
    var emailAddress = "test@example.com";
    var provider = "google";
    var oauthInfo = new OauthInfo(emailAddress, provider);
    when(memberRepository.existsByEmail(any())).thenReturn(true);

    assertThatThrownBy(() -> memberRegistrationService.registerWithOauth(oauthInfo))
        .isInstanceOf(MemberAlreadyExists.class);
  }
}