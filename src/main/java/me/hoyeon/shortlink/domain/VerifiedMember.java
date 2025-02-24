package me.hoyeon.shortlink.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class VerifiedMember {

  private final Long memberId;
  private final Email email;
  private final EncryptedPassword password;
  private final LocalDateTime verifiedAt;

  public static VerifiedMember create(Long memberId, Email email, EncryptedPassword password) {
    return new VerifiedMember(
        memberId,
        email,
        password,
        LocalDateTime.now()
    );
  }

  private VerifiedMember(
      Long memberId,
      Email email,
      EncryptedPassword password,
      LocalDateTime verifiedAt
  ) {
    this.memberId = memberId;
    this.email = email;
    this.password = password;
    this.verifiedAt = verifiedAt;
  }

  public VerifiedMember changePassword(String currentPassword, String newPassword,
      PasswordEncoder encoder) {
    validatePasswordChange(currentPassword, newPassword, encoder);
    return new VerifiedMember(
        this.memberId,
        this.email,
        EncryptedPassword.create(newPassword, encoder),
        this.verifiedAt
    );
  }

  private void validatePasswordChange(String currentPassword, String newPassword,
      PasswordEncoder encoder) {
    validateSamePassword(currentPassword, newPassword);
    validateCurrentPassword(currentPassword, encoder);
  }

  private void validateSamePassword(String currentPassword, String newPassword) {
    if (currentPassword.equals(newPassword)) {
      throw new SamePasswordException();
    }
  }

  private void validateCurrentPassword(String currentPassword, PasswordEncoder encoder) {
    if (!this.password.matches(currentPassword, encoder)) {
      throw new NotCorrectPasswordException();
    }
  }

  public boolean matchPassword(String rawPassword, PasswordEncoder encoder) {
    return this.password.matches(rawPassword, encoder);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VerifiedMember that = (VerifiedMember) o;
    return Objects.equals(memberId, that.memberId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(memberId);
  }
}
