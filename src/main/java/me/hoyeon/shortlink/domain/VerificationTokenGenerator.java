package me.hoyeon.shortlink.domain;

import java.time.Clock;

public interface VerificationTokenGenerator {

  VerificationToken generate(Email email, Clock clock);
}
