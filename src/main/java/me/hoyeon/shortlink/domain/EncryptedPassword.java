package me.hoyeon.shortlink.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EncryptedPassword {

  @Column(nullable = false, name = "encrypted_pwd")
  private String hashedValue;

  public static EncryptedPassword create(String rawPassword, PasswordEncoder encoder) {
    return new EncryptedPassword(encoder.encode(rawPassword));
  }

  protected EncryptedPassword() {}

  private EncryptedPassword(String hashedValue) {
    this.hashedValue = hashedValue;
  }

  public boolean matches(String rawPassword, PasswordEncoder encoder) {
    return encoder.matches(rawPassword, this.hashedValue);
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(hashedValue);
  }

  @Override
  public boolean equals(Object o) {
    return o == this;
  }
}
