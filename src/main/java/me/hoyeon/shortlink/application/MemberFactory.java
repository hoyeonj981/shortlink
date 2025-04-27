package me.hoyeon.shortlink.application;

import java.time.Clock;
import java.util.UUID;
import me.hoyeon.shortlink.domain.Email;
import me.hoyeon.shortlink.domain.EmailValidator;
import me.hoyeon.shortlink.domain.EncryptedPassword;
import me.hoyeon.shortlink.domain.PasswordEncoder;
import me.hoyeon.shortlink.domain.PasswordValidator;
import me.hoyeon.shortlink.domain.UnverifiedMember;
import me.hoyeon.shortlink.domain.VerificationTokenGenerator;
import me.hoyeon.shortlink.domain.VerifiedMember;

public class MemberFactory {

  private final SimpleIdGenerator idGenerator;
  private final VerificationTokenGenerator generator;
  private final EmailValidator emailValidator;
  private final PasswordValidator passwordValidator;
  private final PasswordEncoder passwordEncoder;
  private final Clock clock;

  public MemberFactory(
      SimpleIdGenerator idGenerator,
      VerificationTokenGenerator generator,
      EmailValidator emailValidator,
      PasswordValidator passwordValidator,
      PasswordEncoder passwordEncoder,
      Clock clock
  ) {
    this.idGenerator = idGenerator;
    this.generator = generator;
    this.emailValidator = emailValidator;
    this.passwordValidator = passwordValidator;
    this.passwordEncoder = passwordEncoder;
    this.clock = clock;
  }

  public UnverifiedMember createNew(String emailAddress, String rawPassword) {
    var email = Email.from(emailAddress, emailValidator);
    var encryptedPassword = createEncryptedPassword(rawPassword);
    var verificationToken = generator.generate(email, clock);
    var id = idGenerator.getId();
    return UnverifiedMember.create(id, email, encryptedPassword, verificationToken);
  }

  private EncryptedPassword createEncryptedPassword(String rawPassword) {
    passwordValidator.validate(rawPassword);
    return EncryptedPassword.create(rawPassword, passwordEncoder);
  }

  public VerifiedMember createNewWithOauth(String emailAddress) {
    var email = Email.from(emailAddress, emailValidator);
    var randomPassword = generateRandomPassword();
    return VerifiedMember.create(
        idGenerator.getId(), email, createEncryptedPassword(randomPassword));
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString();
  }
}
