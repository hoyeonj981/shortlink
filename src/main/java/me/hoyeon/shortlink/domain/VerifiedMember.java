package me.hoyeon.shortlink.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "member")
@DiscriminatorValue("verified")
public class VerifiedMember extends MemberBaseEntity implements Member {

  @Id
  private Long memberId;

  @Embedded
  private Email email;

  @Embedded
  private EncryptedPassword password;

  private LocalDateTime verifiedAt;

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
    return true;
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
