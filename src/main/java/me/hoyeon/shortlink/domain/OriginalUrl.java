package me.hoyeon.shortlink.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class OriginalUrl {

  private static final String HTTPS_SCHEME_REGEX = "^https?$";

  private final URI uri;

  public OriginalUrl(String value) {
    try {
      validateNullOrBlank(value);
      uri = new URI(value);
      validateRelativeReference(uri);
      validateAllowedScheme(value);
    } catch (URISyntaxException e) {
      throw new InvalidUrlException(value, e);
    }
  }

  private void validateAllowedScheme(String value) {
    if (!uri.getScheme().toLowerCase().matches(HTTPS_SCHEME_REGEX)) {
      throw new NotAllowedSchemeException(value);
    }
  }

  private void validateRelativeReference(URI uri) {
    if (Objects.isNull(uri.getScheme()) || Objects.isNull(uri.getSchemeSpecificPart())) {
      throw new InvalidUrlException(uri.toString());
    }
  }

  private void validateNullOrBlank(String value) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw new InvalidUrlException(value);
    }
  }

  public String getValue() {
    return this.uri.toString();
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
