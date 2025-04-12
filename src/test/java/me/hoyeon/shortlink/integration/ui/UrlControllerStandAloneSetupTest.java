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
import me.hoyeon.shortlink.application.ShortenedResult;
import me.hoyeon.shortlink.application.UrlShortenerService;
import me.hoyeon.shortlink.ui.CreateShortUrlRequest;
import me.hoyeon.shortlink.ui.UrlController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        .build();
    objectMapper = new ObjectMapper();
  }

  @DisplayName("POST /api/v1/urls - 유효한 요청을 보내면 단축된 URL을 생성 후 응답을 반환한다")
  @Test
  void test() throws Exception {
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

  @DisplayName("GET /api/v1/urls/{alias} - 유효한 요청을 보내면 단축 URL을 반환한다")
  @Test
  void test1() throws Exception {
    var alias = "abcdef";
    var originalUrl = "https://example.com";
    when(urlShortenerService.getOriginalUrl(alias)).thenReturn(originalUrl);

    mockMvc.perform(get("/api/v1/urls/{alias}", alias))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.originalUrl", is(originalUrl)));

    verify(urlShortenerService).getOriginalUrl(alias);
  }
}
