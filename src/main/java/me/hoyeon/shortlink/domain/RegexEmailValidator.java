package me.hoyeon.shortlink.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegexEmailValidator implements EmailValidator {

  private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
  private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

  @Override
  public boolean isValid(String email) {
    if (Objects.isNull(email) || email.isBlank()) {
      return false;
    }
    return PATTERN.matcher(email).matches();
  }

  @Override
  public void validate(String email) throws InvalidEmailException {
    if (!isValid(email)) {
      throw new InvalidEmailException(email);
    }
  }
}
