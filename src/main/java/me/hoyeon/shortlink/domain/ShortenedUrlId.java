package me.hoyeon.shortlink.domain;

import java.util.Objects;

public class ShortenedUrlId {

  private final Long value;

  public ShortenedUrlId(Long value) {
    validateId(value);
    this.value = value;
  }

  private void validateId(Long value) {
    if (Objects.isNull(value) || value <= 0) {
      throw new InvalidUrlIdException(value);
    }
  }

  public Long getValue() {
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
    ShortenedUrlId that = (ShortenedUrlId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
