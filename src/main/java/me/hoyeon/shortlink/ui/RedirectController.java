package me.hoyeon.shortlink.ui;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import me.hoyeon.shortlink.application.RedirectInfo;
import me.hoyeon.shortlink.application.RedirectLoggingService;
import me.hoyeon.shortlink.application.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RedirectController {

  private final UrlShortenerService urlShortenerService;
  private final RedirectLoggingService redirectLoggingService;

  public RedirectController(
      UrlShortenerService urlShortenerService,
      RedirectLoggingService redirectLoggingService
  ) {
    this.urlShortenerService = urlShortenerService;
    this.redirectLoggingService = redirectLoggingService;
  }

  @GetMapping(value = "/{alias}")
  public ResponseEntity<Void> redirectToOriginalUrl(
      @PathVariable String alias,
      HttpServletRequest request
  ) {
    var originalUrl = urlShortenerService.getOriginalUrl(alias);
    var redirectInfo = new RedirectInfo(
        alias,
        originalUrl,
        request.getRemoteAddr(),
        request.getHeader("User-Agent"),
        request.getHeader("Referer")
    );
    redirectLoggingService.log(redirectInfo);
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(originalUrl))
        .build();
  }
}
