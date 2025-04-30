package me.hoyeon.shortlink.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "member")
@SecondaryTable(
    name = "verification_token",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "member_id")
)
@DiscriminatorValue("unverified")
public class UnverifiedMember extends MemberBaseEntity implements Member {

  @Id
  private Long memberId;

  @Embedded
  private Email email;

  @Embedded
  private EncryptedPassword password;

  @Embedded
  private VerificationToken token;

  public static UnverifiedMember create(Long id, Email email, EncryptedPassword password,
      VerificationToken token) {
    return new UnverifiedMember(id, email, password, token);
  }

  protected UnverifiedMember() {}

  private UnverifiedMember(
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
