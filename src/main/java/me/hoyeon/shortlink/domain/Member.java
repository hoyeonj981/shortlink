package me.hoyeon.shortlink.domain;

public interface Member {

  Long getId();

  Email getEmail();

  boolean matchPassword(String rawPassword, PasswordEncoder passwordEncoder);

  boolean isVerified();
}
