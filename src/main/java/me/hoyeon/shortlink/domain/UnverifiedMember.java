package me.hoyeon.shortlink.domain;

import java.util.Objects;

public class UnverifiedMember implements Member {

  private final Long memberId;
  private final Email email;
  private final EncryptedPassword password;
  private final VerificationToken token;

  public static UnverifiedMember create(Long id, Email email, EncryptedPassword password,
      VerificationToken token) {
    return new UnverifiedMember(id, email, password, token);
  }

  public UnverifiedMember(
      Long memberId,
      Email email,
      EncryptedPassword password,
      VerificationToken token
  ) {
    this.memberId = memberId;
    this.email = email;
    this.password = password;
    this.token = token;
  }

  public VerifiedMember verify(String token) {
    try {
      this.token.verify(token);
    } catch (TokenExpiredException | TokenMismatchException e) {
      throw new VerificationFailedException("토큰 검증에 실패했습니다", e);
    }
    return VerifiedMember.create(this.memberId, this.email, this.password);
  }

  public String getTokenString() {
    return this.token.getTokenValue();
  }

  public String getEmailString() {
    return this.email.getAddress();
  }

  @Override
  public boolean matchPassword(String rawPassword, PasswordEncoder encoder) {
    return this.password.matches(rawPassword, encoder);
  }

  @Override
  public Long getId() {
    return this.memberId;
  }

  @Override
  public Email getEmail() {
    return this.email;
  }

  @Override
  public boolean isVerified() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnverifiedMember that = (UnverifiedMember) o;
    return Objects.equals(memberId, that.memberId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(memberId);
  }
}
