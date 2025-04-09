package me.hoyeon.shortlink.infrastructure;

public interface JwtRepository {

  void addToBlackList(String token, long expireTime);

  boolean isBlackListed(String token);
}
