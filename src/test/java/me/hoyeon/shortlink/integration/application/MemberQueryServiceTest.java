package me.hoyeon.shortlink.integration.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.hoyeon.shortlink.application.MemberNotFoundException;
import me.hoyeon.shortlink.application.MemberQueryService;
import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import me.hoyeon.shortlink.domain.Member;
import me.hoyeon.shortlink.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private EmailValidator emailValidator;

  @InjectMocks
  private MemberQueryService memberQueryService;

  @DisplayName("이메일로 회원 조회 시 해당 회원이 있으면 성공적으로 회원을 반환한다")
  @Test
  void getMemberByEmailSuccessfully() {
    var emailAddress = "test@example.com";
    doNothing().when(emailValidator).validate(emailAddress);
    var email = Email.from(emailAddress, emailValidator);
    var member = mock(Member.class);
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

    var result = memberQueryService.getMemberByEmail(emailAddress);

    assertThat(result).isEqualTo(member);
    verify(memberRepository).findByEmail(email);
  }

  @DisplayName("이메일로 회원 조회 시 해당 회원이 없으면 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberNotFoundDuringGetMemberByEmail() {
    var emailAddress = "test@example.com";
    doNothing().when(emailValidator).validate(emailAddress);
    var email = Email.from(emailAddress, emailValidator);
    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> memberQueryService.getMemberByEmail(emailAddress))
        .isInstanceOf(MemberNotFoundException.class);
    verify(memberRepository).findByEmail(email);
  }

  @DisplayName("이메일 형식이 올바르지 않으면 예외가 발생한다")
  @Test
  void throwExceptionWhenEmailFormatIsInvalid() {
    var invalidEmail = "invalid-email";
    when(Email.from(invalidEmail, emailValidator))
        .thenThrow(new InvalidEmailException(invalidEmail));

    assertThatThrownBy(() -> memberQueryService.getMemberByEmail(invalidEmail))
        .isInstanceOf(InvalidEmailException.class);
  }

  @DisplayName("ID로 회원 조회 시 해당 회원이 있으면 성공적으로 회원을 반환한다")
  @Test
  void getMemberByIdSuccessfully() {
    var memberId = 1L;
    var member = mock(Member.class);
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    var result = memberQueryService.getMemberById(memberId);

    assertThat(result).isEqualTo(member);
    verify(memberRepository).findById(memberId);
  }

  @DisplayName("ID로 회원 조회 시 해당 회원이 없으면 예외가 발생한다")
  @Test
  void throwExceptionWhenMemberNotFoundDuringGetMemberById() {
    var memberId = 1L;
    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> memberQueryService.getMemberById(memberId))
        .isInstanceOf(MemberNotFoundException.class);
    verify(memberRepository).findById(memberId);
  }
}