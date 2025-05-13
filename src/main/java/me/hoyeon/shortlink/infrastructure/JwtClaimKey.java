package me.hoyeon.shortlink.infrastructure;

public enum JwtClaimKey {

  MEMBER_ID("member_id"),
  ROLE("role"),
  TOKEN_TYPE("token_type");

  private final String claimName;

  JwtClaimKey(String claimName) {
    this.claimName = claimName;
  }

  public String getClaimName() {
    return claimName;
  }
}
