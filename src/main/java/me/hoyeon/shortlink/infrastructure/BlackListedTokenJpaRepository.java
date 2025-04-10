package me.hoyeon.shortlink.infrastructure;

public interface BlackListedTokenJpaRepository {

  BlackListedToken save(BlackListedToken blackListedToken);

  boolean existsByToken(String token);
}
