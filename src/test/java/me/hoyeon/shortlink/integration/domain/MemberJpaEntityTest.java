package me.hoyeon.shortlink.integration.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EncryptedPassword;
import me.hoyeon.shortlink.domain.PasswordEncoder;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import me.hoyeon.shortlink.domain.VerificationToken;
import me.hoyeon.shortlink.domain.VerifiedMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaTestConfig.class})
public class MemberJpaEntityTest {

  private static final Clock FIXED_CLOCK = Clock.fixed(
      Instant.parse("2023-01-01T00:00:00Z"), ZoneId.systemDefault());

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TestEntityManager testEntityManager;

  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    passwordEncoder = mock(PasswordEncoder.class);
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
  }

  @DisplayName("미인증 회원은 JPA 엔티티로 저장되고 조회된다")
  @Test
  void findUnverifiedMemberWithJpaEntity() {
    var testToken = "test-token";
    var expirationMin = 30L;
    var token = VerificationToken.create(testToken, expirationMin, FIXED_CLOCK);
    var email = Email.of("test@example.com");
    var password = EncryptedPassword.create("password", passwordEncoder);
    var memberId = 1L;
    var unverifiedMember = UnverifiedMember.create(memberId, email, password, token);

    entityManager.persist(unverifiedMember);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(UnverifiedMember.class, memberId);
    assertThat(found).isNotNull();
    assertThat(found.getEmail()).isEqualTo(email);
    assertThat(found.getTokenString()).isEqualTo(testToken);
  }

  @DisplayName("인증된 회원은 JPA 엔티티로 저장되고 조회된다")
  @Test
  void findVerifiedMemberWithJpaEntity() {
    var email = Email.of("test@example.com");
    var password = EncryptedPassword.create("password", passwordEncoder);
    var memberId = 1L;
    var verifiedMember = VerifiedMember.create(memberId, email, password);

    entityManager.persist(verifiedMember);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(VerifiedMember.class, memberId);
    assertThat(found).isNotNull();
    assertThat(found.getEmail()).isEqualTo(email);
    assertThat(found.isVerified()).isTrue();
  }

  @DisplayName("미인증 회원은 인증 후 인증 회원으로 변환되어 저장된다")
  @Test
  void unverifiedMemberCanBeVerifiedWithJpaEntity() {
    var testToken = "test-token";
    var expirationMin = 30L;
    var token = VerificationToken.create(testToken, expirationMin, FIXED_CLOCK);
    var email = Email.of("test@example.com");
    var password = EncryptedPassword.create("password", passwordEncoder);
    var memberId = 1L;
    var unverifiedMember = UnverifiedMember.create(memberId, email, password, token);

    entityManager.persist(unverifiedMember);
    entityManager.flush();

    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    unverifiedMember.verify(testToken);
    entityManager.createNativeQuery(
            "UPDATE member SET member_type = 'verified' WHERE member_id = :id")
        .setParameter("id", memberId)
        .executeUpdate();

    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(VerifiedMember.class, memberId);
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(memberId);
    assertThat(found.getEmail()).isEqualTo(email);
    assertThat(found.isVerified()).isTrue();
  }

  @DisplayName("인증된 회원은 비밀번호를 변경할 수 있다")
  @Test
  void verifyMemberCanChangePassword() {
    var email = Email.of("password-change@example.com");
    var oldPassword = "old-password";
    var newPassword = "new-password";
    var encodedNewPassword = "encoded-new-password";
    var password = EncryptedPassword.create(oldPassword, passwordEncoder);
    var memberId = 1L;
    var verifiedMember = VerifiedMember.create(memberId, email, password);

    entityManager.persist(verifiedMember);
    entityManager.flush();

    when(passwordEncoder.matches(eq(oldPassword), anyString())).thenReturn(true);
    when(passwordEncoder.encode(eq(newPassword))).thenReturn(encodedNewPassword);
    var updatedMember = verifiedMember.changePassword(oldPassword, newPassword, passwordEncoder);

    entityManager.detach(verifiedMember);
    entityManager.merge(updatedMember);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(VerifiedMember.class, memberId);
    when(passwordEncoder.matches(eq(newPassword), anyString())).thenReturn(true);
    when(passwordEncoder.matches(eq(oldPassword), anyString())).thenReturn(false);
    assertThat(found.matchPassword(newPassword, passwordEncoder)).isTrue();
    assertThat(found.matchPassword(oldPassword, passwordEncoder)).isFalse();
  }
}
