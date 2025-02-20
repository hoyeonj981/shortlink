package me.hoyeon.shortlink.application;

import me.hoyeon.shortlink.domain.UrlMappingRepository;

public class UrlShortenerService {

  private final UrlMappingRepository mappingRepository;
  private final ShortenedUrlCreator creator;

  public UrlShortenerService(
      UrlMappingRepository mappingRepository,
      ShortenedUrlCreator creator
  ) {
    this.mappingRepository = mappingRepository;
    this.creator = creator;
  }

  public ShortenedResult shortenUrl(String originalUrl) {
    if (mappingRepository.isExistingByOriginalUrl(originalUrl)) {
      throw new DuplicateUrlException(originalUrl);
    }

    var shortenedUrl = creator.create(originalUrl);
    mappingRepository.save(shortenedUrl);
    return new ShortenedResult(
        shortenedUrl.getOriginalUrlToString(),
        shortenedUrl.getAliasToString()
    );
  }

  public String getOriginalUrl(String alias) {
    return mappingRepository.findByAlias(alias)
        .map(url -> {
          var originalUrl = url.getOriginalUrlToString();
          if (!url.isAccessible()) {
            throw new NotAccsibleUrlException(originalUrl);
          }
          return originalUrl;
        })
        .orElseThrow(() -> new UrlNotFoundException(alias));
  }
}
