package me.hoyeon.shortlink.infrastructure;

public class ClaimNotExistException extends RuntimeException {

  public static final String MESSAGE = "Claim '%s' does not exist";

  public ClaimNotExistException(String key) {
    super(String.format(MESSAGE, key));
  }
}
