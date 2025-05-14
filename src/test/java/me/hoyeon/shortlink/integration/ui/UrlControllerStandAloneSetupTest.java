package me.hoyeon.shortlink.integration.ui;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hoyeon.shortlink.application.DuplicateUrlException;
import me.hoyeon.shortlink.application.NotAccessibleUrlException;
import me.hoyeon.shortlink.application.ShortenedResult;
import me.hoyeon.shortlink.application.UrlNotFoundException;
import me.hoyeon.shortlink.application.UrlShortenerService;
import me.hoyeon.shortlink.infrastructure.config.GlobalExceptionHandler;
import me.hoyeon.shortlink.ui.CreateShortUrlRequest;
import me.hoyeon.shortlink.ui.UrlController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class UrlControllerStandAloneSetupTest {

  private MockMvc mockMvc;

  private UrlShortenerService urlShortenerService;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    urlShortenerService = mock(UrlShortenerService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new UrlController(urlShortenerService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    objectMapper = new ObjectMapper();
  }

  @DisplayName("요청 성공")
  @Nested
  class SuccessCases {

    @DisplayName("POST /api/v1/urls - 유효한 요청을 보내면 단축된 URL을 생성 후 응답을 반환한다")
    @Test
    void returnShortUrlResponseIfRequestIsValid() throws Exception {
      var originalUrl = "https://example.com";
      var alias = "abcdef";
      var request = new CreateShortUrlRequest(originalUrl);
      when(urlShortenerService.shortenUrl(any())).thenReturn(new ShortenedResult(originalUrl, alias));

      mockMvc.perform(post("/api/v1/urls")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.originalUrl", is(originalUrl)))
          .andExpect(jsonPath("$.alias", is(alias)));

      verify(urlShortenerService).shortenUrl(any());
    }

    @DisplayName("GET /api/v1/urls/{alias} - 유효한 요청을 보내면 단축 URL의 원본을 반환한다")
    @Test
    void getOriginalUrlIfRequestIsValid() throws Exception {
      var alias = "abcdef";
      var originalUrl = "https://example.com";
      when(urlShortenerService.getOriginalUrl(alias)).thenReturn(originalUrl);

      mockMvc.perform(get("/api/v1/urls/{alias}", alias))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.originalUrl", is(originalUrl)));

      verify(urlShortenerService).getOriginalUrl(alias);
    }
  }

  @DisplayName("요청 실패")
  @Nested
  class FailureCases {

    @DisplayName("GET /api/v1/urls/{alias} - 존재하지 않는 alias는 404 에러를 반환한다")
    @Test
    void return404IfAliasDoesNotExist() throws Exception {
      var alias = "abcdef";
      when(urlShortenerService.getOriginalUrl(alias)).thenThrow(UrlNotFoundException.class);

      mockMvc.perform(get("/api/v1/urls/{alias}", alias))
          .andExpect(status().isNotFound());
    }

    @DisplayName("POST /api/v1/urls - 중복된 URL 요청은 404 Bad Request를 반환한다")
    @Test
    void return404IfUrlIsDuplicated() throws Exception {
      var duplicatedUrl = "https://example.com";
      var request = new CreateShortUrlRequest(duplicatedUrl);
      when(urlShortenerService.shortenUrl(any())).thenThrow(DuplicateUrlException.class);

      mockMvc.perform(post("/api/v1/urls")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /api/v1/urls/{alias} - 만료된 alias는 410 에러를 반환한다")
    @Test
    void return401IfAliasIsExpired() throws Exception {
      var alias = "abcdef";
      when(urlShortenerService.getOriginalUrl(alias)).thenThrow(NotAccessibleUrlException.class);

      mockMvc.perform(get("/api/v1/urls/{alias}", alias))
          .andExpect(status().isGone());
      verify(urlShortenerService).getOriginalUrl(alias);
    }
  }
}
