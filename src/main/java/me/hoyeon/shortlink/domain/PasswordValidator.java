package me.hoyeon.shortlink.domain;

public interface PasswordValidator {

  boolean isValid(String rawPassword);

  void validate(String rawPassword) throws InvalidPasswordException;
}
