package me.hoyeon.shortlink.ui;

import me.hoyeon.shortlink.application.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

  private final UrlShortenerService urlShortenerService;

  public UrlController(UrlShortenerService urlShortenerService) {
    this.urlShortenerService = urlShortenerService;
  }

  @PostMapping
  public ResponseEntity<ShortUrlResponse> createShortUrl(
      @RequestBody CreateShortUrlRequest request
  ) {
    var result = urlShortenerService.shortenUrl(request.originalUrl());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ShortUrlResponse(
            result.originalUrl(),
            result.shortenedUrl()
        ));
  }

  @GetMapping(value = "/{alias}")
  public ResponseEntity<OriginalUrlResponse> getOriginalUrl(@PathVariable String alias) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(new OriginalUrlResponse(
            urlShortenerService.getOriginalUrl(alias)
        ));
  }
}
