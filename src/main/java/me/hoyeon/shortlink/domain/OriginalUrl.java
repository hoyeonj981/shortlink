package me.hoyeon.shortlink.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class OriginalUrl {

  private final URI uri;

  public OriginalUrl(String value) {
    try {
      validateNullOrBlank(value);
      uri = new URI(value);
      validateRelativeReference(uri);
    } catch (URISyntaxException e) {
      throw new InvalidUrlException(value, e);
    }
  }

  private void validateRelativeReference(URI uri) {
    if (Objects.isNull(uri.getScheme()) || Objects.isNull(uri.getHost())) {
      throw new InvalidUrlException(uri.toString());
    }
  }

  private void validateNullOrBlank(String value) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw new InvalidUrlException(value);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OriginalUrl that = (OriginalUrl) o;
    return Objects.equals(uri, that.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri);
  }
}
