package me.hoyeon.shortlink.domain;

import java.util.Objects;

public class SimpleAliasGenerator implements AliasGenerator {

  private static final int FIXED_LENGTH = 6;
  private static final String BASE62
      = "0123456789"
      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      + "abcdefghijklmnopqrstuvwxyz";

  @Override
  public Alias shorten(String base) {
    var hashed = Objects.hashCode(base);
    return convertHashToAlias(hashed);
  }

  private Alias convertHashToAlias(int hashed) {
    var encodedValue = encodeBase62(hashed);
    return new Alias(encodedValue);
  }

  private String encodeBase62(int value) {
    var unsignedValue = Integer.toUnsignedLong(value);
    var result = new StringBuilder();

    do {
      result.insert(0, BASE62.charAt((int) (unsignedValue % 62)));
      unsignedValue /= 62;
    } while (unsignedValue > 0);

    while (result.length() < FIXED_LENGTH) {
      result.insert(0, 0);
    }

    return result.toString();
  }
}
