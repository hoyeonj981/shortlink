package me.hoyeon.shortlink.domain;

public interface PasswordEncoder {

  String encode(String rawPassword);

  boolean matches(String rawPassword, String hashedValue);
}
