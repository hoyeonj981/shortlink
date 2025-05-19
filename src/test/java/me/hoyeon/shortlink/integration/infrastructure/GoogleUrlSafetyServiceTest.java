package me.hoyeon.shortlink.integration.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.util.List;
import me.hoyeon.shortlink.application.ExternalApiException;
import me.hoyeon.shortlink.domain.OriginalUrl;
import me.hoyeon.shortlink.infrastructure.GoogleUrlSafetyService;
import me.hoyeon.shortlink.infrastructure.config.GoogleUrlSafetyProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

class GoogleUrlSafetyServiceTest {

  private static MockWebServer mockWebServer;

  private GoogleUrlSafetyService googleUrlSafetyService;

  @BeforeAll
  static void startMockWebServer() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void stopMockWebServer() throws Exception {
    mockWebServer.shutdown();
  }

  @BeforeEach
  void setup() {
    var prop = new GoogleUrlSafetyProperties();
    prop.setEndpoint(mockWebServer.url("/safebrowsing").toString());
    prop.setApiKey("testkey");
    prop.setClientId("clientId");
    prop.setClientVersion("1.0");
    prop.setThreatTypes(List.of("MALWARE"));
    prop.setPlatformTypes(List.of("WINDOWS"));
    prop.setEntryTypes(List.of("URL"));
    var restClient = RestClient.create();
    googleUrlSafetyService = new GoogleUrlSafetyService(prop, restClient);
  }

  @DisplayName("요청에 대해서 정상 응답을 반환한다")
  @Test
  void responseSuccessfully() throws Exception {
    var responseBody = "{\"matches\":[]}";
    mockWebServer.enqueue(new MockResponse()
        .setBody(responseBody)
        .addHeader("Content-Type", "application/json")
        .setResponseCode(200));
    var urls = List.of(new OriginalUrl("https://example.com"));

    var info = googleUrlSafetyService.assess(urls);

    assertThat(info).isNotNull();
  }

  @DisplayName("서버가 4xx 응답을 반환하면 예외가 발생한다")
  @Test
  void throwExceptionWhenServerReturns4xx() throws Exception {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(400)
        .setBody("{\"error\":\"bad request\"}"));

     var urls = List.of(new OriginalUrl("https://example.com"));

    assertThatThrownBy(() -> googleUrlSafetyService.assess(urls))
        .isInstanceOf(ExternalApiException.class);
  }

  @DisplayName("서버가 5xx 응답을 반환하면 예외가 발생한다")
  @Test
  void throwExceptionWhenServerReturns5xx() throws Exception {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(500)
        .setBody("{\"error\":\"server error\"}"));

     var urls = List.of(new OriginalUrl("https://example.com"));

    assertThatThrownBy(() -> googleUrlSafetyService.assess(urls))
        .isInstanceOf(ExternalApiException.class);
  }

  @DisplayName("IO 문제가 발생하면 예외가 발생한다")
  @Test
  void throwExceptionWhenNetworkErrorOccur() throws Exception {
    mockWebServer.shutdown();
    var urls = List.of(new OriginalUrl("https://example.com"));

    assertThatThrownBy(() -> googleUrlSafetyService.assess(urls))
        .isInstanceOf(ExternalApiException.class)
        .hasCauseInstanceOf(ResourceAccessException.class);
  }
}