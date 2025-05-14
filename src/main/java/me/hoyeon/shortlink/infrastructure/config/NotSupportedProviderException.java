package me.hoyeon.shortlink.infrastructure.config;

public class NotSupportedProviderException extends RuntimeException {

  private static final String MESSAGE = "'%s' provider is not supported";

  public NotSupportedProviderException(String notSupportedProvider) {
    super(String.format(MESSAGE, notSupportedProvider));
  }
}
