package me.hoyeon.shortlink.domain;

public interface EmailValidator {

  boolean isValid(String email);

  void validate(String email) throws InvalidEmailException;
}
