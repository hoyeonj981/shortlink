package me.hoyeon.shortlink.domain;

import java.util.Objects;

public class Email {

  private static final EmailValidator DEFAULT_VALIDATOR = new RegexEmailValidator();

  private final String address;

  public static Email of(String emailAddress) {
    return Email.from(emailAddress, DEFAULT_VALIDATOR);
  }

  public static Email from(String emailAddress, EmailValidator validator) {
    validator.validate(emailAddress);
    return new Email(emailAddress);
  }

  private Email(String address) {
    this.address = address.toLowerCase();
  }

  public String getAddress() {
    return address;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Email email = (Email) o;
    return Objects.equals(address, email.address);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(address);
  }
}
