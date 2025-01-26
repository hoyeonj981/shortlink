package me.hoyeon.shortlink.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class Alias {

  private static final int FIXED_LENGTH = 6;
  private static final String VALID_CHARACTERS_PATTERN = "^[a-zA-Z0-9]+$";
  private static final Pattern VALID_PATTERN = Pattern.compile(VALID_CHARACTERS_PATTERN);

  private final String value;

  public Alias(String value) {
    validateAlias(value);
    this.value = value;
  }

  private void validateAlias(String value) {
    validateNullOrEmpty(value);
    validateLength(value);
    validatePattern(value);
  }

  private void validateNullOrEmpty(String value) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw new InvalidAliasException();
    }
  }

  private void validateLength(String value) {
    if (value.length() != FIXED_LENGTH) {
      throw new InvalidAliasException(value);
    }
  }

  private void validatePattern(String value) {
    if (!VALID_PATTERN.matcher(value).matches()) {
      throw new InvalidAliasException(value);
    }
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Alias alias = (Alias) o;
    return Objects.equals(value, alias.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
