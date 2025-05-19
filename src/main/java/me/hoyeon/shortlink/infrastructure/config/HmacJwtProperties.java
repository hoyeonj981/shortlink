package me.hoyeon.shortlink.infrastructure.config;

public class HmacJwtProperties {

  private String issuer;
  private String secret;
  private String algorithm;
  private long accessExpiration;
  private long refreshExpiration;

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public long getAccessExpiration() {
    return accessExpiration;
  }

  public void setAccessExpiration(long accessExpiration) {
    this.accessExpiration = accessExpiration;
  }

  public long getRefreshExpiration() {
    return refreshExpiration;
  }

  public void setRefreshExpiration(long refreshExpiration) {
    this.refreshExpiration = refreshExpiration;
  }
}