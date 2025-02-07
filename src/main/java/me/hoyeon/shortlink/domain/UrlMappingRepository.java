package me.hoyeon.shortlink.domain;

import java.util.Optional;

public interface UrlMappingRepository {

  void save(ShortenedUrl shortenedUrl);

  Optional<ShortenedUrl> findByAlias(String alias);

  boolean isExistingByOriginalUrl(String originalUrl);
}
