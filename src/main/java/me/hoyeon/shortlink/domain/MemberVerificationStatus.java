package me.hoyeon.shortlink.domain;

public enum MemberVerificationStatus {
  VERIFIED("verified"),
  UNVERIFIED("unverified")
  ;

  private final String value;

  public static MemberVerificationStatus of(Member member) {
    return member.isVerified() ? VERIFIED : UNVERIFIED;
  }

  MemberVerificationStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
