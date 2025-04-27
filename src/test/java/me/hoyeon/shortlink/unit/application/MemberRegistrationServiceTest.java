package me.hoyeon.shortlink.unit.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import me.hoyeon.shortlink.application.MemberFactory;
import me.hoyeon.shortlink.application.MemberRegistrationService;
import me.hoyeon.shortlink.application.MemberVerificationService;
import me.hoyeon.shortlink.application.OAuthCredentialRepository;
import me.hoyeon.shortlink.domain.MemberRepository;
import me.hoyeon.shortlink.domain.UnverifiedMember;
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
  private OAuthCredentialRepository oauthCredentialRepository;

  @InjectMocks
  private MemberRegistrationService memberRegistrationService;

  @DisplayName("이메일로 회원가입 시 새로운 미회원을 생성하고 인증 이메일을 전송한다")
  @Test
  void test() {
    var emailAddress = "test@example.com";
    var rawPassword = "test-password";
    var memberId = 1L;
    var unverifiedMember = mock(UnverifiedMember.class);
    when(unverifiedMember.getId()).thenReturn(memberId);
    when(memberFactory.createNew(any(), any())).thenReturn(unverifiedMember);
    when(memberRepository.existsByEmail(any())).thenReturn(false);

    memberRegistrationService.registerWithEmail(emailAddress, rawPassword);

    verify(memberRepository).save(unverifiedMember);
    verify(memberVerificationService).sendVerificationMail(memberId);
  }

  @DisplayName("이메일로 회원가입 시 이미 존재하는 이메일이면 예외가 발생한다")
  @Test
  void test1() {
  }

  @DisplayName("OAuth로 회원가입 시 새로운 인증회원을 생성하고 OAuth 정보를 저장한다")
  @Test
  void test2() {
  }

  @DisplayName("OAuth로 회원가입 시 이미 존재하는 이메일이면 예외가 발생한다")
  @Test
  void test3() {
  }
}