package me.hoyeon.shortlink.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.hoyeon.shortlink.domain.MemberRepository;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import me.hoyeon.shortlink.domain.VerifiedMember;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberVerificationServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_TOKEN = "test-token";

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private EmailSender emailSender;

  @InjectMocks
  private MemberVerificationService verificationService;

  @DisplayName("인증 이메일 전송에 성공한다")
  @Test
  void sendVerificationEmailSuccessfully() {
    var givenMemberId = 1L;
    var unverifiedMember = mock(UnverifiedMember.class);
    when(unverifiedMember.getEmailString()).thenReturn(TEST_EMAIL);
    when(unverifiedMember.getTokenString()).thenReturn(TEST_TOKEN);
    when(memberRepository.findUnverifiedById(anyLong())).thenReturn(Optional.of(unverifiedMember));

    verificationService.sendVerificationMail(givenMemberId);

    verify(emailSender).send(
        eq(TEST_EMAIL),
        eq("Test Verification Subject"),
        eq(TEST_TOKEN)
    );
  }

  @DisplayName("존재하지 않는 회원으로 인증 이메일을 전송할 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberNotFoundDuringSendingVerificationEmail() {
    var givenMemberId = 1L;
    when(memberRepository.findUnverifiedById(anyLong())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> verificationService.sendVerificationMail(givenMemberId))
        .isInstanceOf(MemberNotFoundException.class);
  }

  @DisplayName("주어진 인증 토큰으로 회원 인증에 성공한다")
  @Test
  void verifyMemberSuccessfully() {
    var givenMemberId = 1L;
    var unverifiedMember = mock(UnverifiedMember.class);
    var verifiedMember = mock(VerifiedMember.class);
    when(memberRepository.findUnverifiedById(anyLong())).thenReturn(Optional.of(unverifiedMember));
    when(unverifiedMember.verify(anyString())).thenReturn(verifiedMember);

    var result = verificationService.verify(givenMemberId, TEST_TOKEN);

    assertThat(result).isEqualTo(verifiedMember);
    verify(memberRepository).findUnverifiedById(givenMemberId);
    verify(unverifiedMember).verify(TEST_TOKEN);
  }

  @DisplayName("존재하지 않는 회원으로 인증할 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberNotFoundDuringVerifying() {
    var givenMemberId = 1L;
    when(memberRepository.findUnverifiedById(anyLong())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> verificationService.verify(givenMemberId, TEST_TOKEN))
        .isInstanceOf(MemberNotFoundException.class);
  }
}