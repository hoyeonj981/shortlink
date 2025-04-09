package me.hoyeon.shortlink.infrastructure;

public interface JwtRepository {

  String addToBlackList(String token, long expireTime);

  boolean isBlackListed(String token);
}
