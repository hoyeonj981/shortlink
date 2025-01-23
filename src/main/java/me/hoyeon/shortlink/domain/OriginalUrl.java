package me.hoyeon.shortlink.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public record OriginalUrl(
    String value
) {

  public OriginalUrl {
    validateUrl(value);
  }

  private void validateUrl(String value) {
    if (Objects.isNull(value) || value.isBlank()) {
      throw new InvalidUrlException(value);
    }

    try {
      var url = new URL(value);
      validateHost(url);
    } catch (MalformedURLException e) {
      throw new InvalidUrlException(value, e);
    }
  }

  private void validateHost(URL url) {
    if (url.getHost().isBlank()) {
      throw new InvalidUrlException(url.toString());
    }
  }
}
