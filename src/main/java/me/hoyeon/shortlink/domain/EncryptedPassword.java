package me.hoyeon.shortlink.domain;

import java.util.Objects;

public class EncryptedPassword {

  private final String hashedValue;

  public static EncryptedPassword create(String rawPassword, PasswordEncoder encoder) {
    return new EncryptedPassword(encoder.encode(rawPassword));
  }

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
