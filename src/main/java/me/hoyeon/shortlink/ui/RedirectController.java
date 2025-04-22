package me.hoyeon.shortlink.ui;

import java.net.URI;
import me.hoyeon.shortlink.application.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RedirectController {

  private final UrlShortenerService urlShortenerService;

  public RedirectController(UrlShortenerService urlShortenerService) {
    this.urlShortenerService = urlShortenerService;
  }

  @GetMapping(value = "/{alias}")
  public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String alias) {
    var originalUrl = urlShortenerService.getOriginalUrl(alias);
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(originalUrl))
        .build();
  }
}
