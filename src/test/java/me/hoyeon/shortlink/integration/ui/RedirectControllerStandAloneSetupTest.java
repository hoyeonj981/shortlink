package me.hoyeon.shortlink.integration.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.hoyeon.shortlink.application.NotAccessibleUrlException;
import me.hoyeon.shortlink.application.RedirectLoggingService;
import me.hoyeon.shortlink.application.UrlNotFoundException;
import me.hoyeon.shortlink.application.UrlShortenerService;
import me.hoyeon.shortlink.infrastructure.config.GlobalExceptionHandler;
import me.hoyeon.shortlink.ui.RedirectController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class RedirectControllerStandAloneSetupTest {

  private MockMvc mockMvc;

  private UrlShortenerService urlShortenerService;

  private RedirectLoggingService redirectLoggingService;

  @BeforeEach
  void setUp() {
    urlShortenerService = mock(UrlShortenerService.class);
    redirectLoggingService = mock(RedirectLoggingService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(
          new RedirectController(urlShortenerService, redirectLoggingService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @DisplayName("요청 성공")
  @Nested
  class SuccessCases {

    @DisplayName("GET /{alias} - redirect에 성공하여 302를 반환한다")
    @Test
    void redirectAliasToOriginalUrl() throws Exception {
      var alias = "abcdef";
      var originalUrl = "https://example.com";
      when(urlShortenerService.getOriginalUrl(alias)).thenReturn(originalUrl);

      mockMvc.perform(get("/{alias}", alias))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(originalUrl));

      verify(redirectLoggingService).log(any());
    }
  }

  @DisplayName("요청 실패")
  @Nested
  class FailureCases {

    @DisplayName("GET /{alias} - 존재하지 않는 alias는 404를 반환한다")
    @Test
    void return404IfAliasDoesNotExist() throws Exception {
      var alias = "abcdef";
      when(urlShortenerService.getOriginalUrl(alias)).thenThrow(UrlNotFoundException.class);

      mockMvc.perform(get("/{alias}", alias))
          .andExpect(status().isNotFound());
    }

    @DisplayName("GET /{alias} - 만료된 alias는 410를 반환한다")
    @Test
    void return410IfAliasIsExpired() throws Exception {
      var alias = "abcdef";
      when(urlShortenerService.getOriginalUrl(alias)).thenThrow(NotAccessibleUrlException.class);

      mockMvc.perform(get("/{alias}", alias))
          .andExpect(status().isGone());
    }
  }
}
