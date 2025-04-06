package me.hoyeon.shortlink.application;

import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.InvalidEmailException;
import me.hoyeon.shortlink.domain.MemberRepository;
import me.hoyeon.shortlink.domain.PasswordEncoder;

public class AuthenticationService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailValidator emailValidator;

  public AuthenticationService(
      MemberRepository memberRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider,
      EmailValidator emailValidator
  ) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.emailValidator = emailValidator;
  }

  public SignInResponse signIn(String rawEmail, String rawPassword) {
    try {
      var email = Email.from(rawEmail, emailValidator);
      var member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
      if (!member.matchPassword(rawPassword, passwordEncoder)) {
        throw new MismatchPasswordException();
      }
      return new SignInResponse(
          jwtTokenProvider.generateAccessToken(member.getId()),
          jwtTokenProvider.generateRefreshToken(member.getId())
      );
    } catch (InvalidEmailException e) {
      throw new AuthenticationException(e.getMessage(), e);
    }
  }

  public void signOut(String jwtToken) {
    jwtTokenProvider.validate(jwtToken);
    jwtTokenProvider.invalidate(jwtToken);
  }
}
