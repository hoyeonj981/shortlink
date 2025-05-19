package me.hoyeon.shortlink.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.hoyeon.shortlink.application.ExternalApiException;
import me.hoyeon.shortlink.application.ThreatInfo;
import me.hoyeon.shortlink.application.UrlSafetyService;
import me.hoyeon.shortlink.domain.OriginalUrl;
import me.hoyeon.shortlink.infrastructure.config.GoogleUrlSafetyProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class GoogleUrlSafetyService implements UrlSafetyService {

  private static final String KEY_QUERY = "?key=";

  private final GoogleUrlSafetyProperties properties;
  private final RestClient restClient;

  public GoogleUrlSafetyService(GoogleUrlSafetyProperties properties, RestClient restClient) {
    this.properties = properties;
    this.restClient = restClient;
  }

  @Override
  public ThreatInfo assess(List<OriginalUrl> urls) {
    validateUrls(urls);
    try {
      var requestBody = buildRequestBody(urls);
      var url = properties.getEndpoint() + KEY_QUERY + properties.getApiKey();

      return restClient.post()
          .uri(url)
          .contentType(MediaType.APPLICATION_JSON)
          .body(requestBody)
          .retrieve()
          .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
            var message = String.format(
                "Google safe browsing, code: %s, uri: %s",
                response.getStatusCode(),
                request.getURI());
            throw new ExternalApiException(message);
          }))
          .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
            var message = String.format(
                "Google safe browsing, code: %s, uri: %s",
                response.getStatusCode(),
                request.getURI());
            throw new ExternalApiException(message);
          }))
          .body(ThreatInfo.class);
    } catch (RestClientException e) {
      throw new ExternalApiException("Google safe browsing api error", e);
    }
  }

  private void validateUrls(List<OriginalUrl> urls) {
    if (urls == null || urls.isEmpty()) {
      throw new  IllegalArgumentException("urls is null or empty");
    }
  }

  private Map<String, Object> buildRequestBody(List<OriginalUrl> urls) {
    var apiUrl = properties.getEndpoint() + KEY_QUERY + properties.getApiKey();

    var requestBody = new HashMap<String, Object>();

    var client = new HashMap<String, Object>();
    client.put("clientId", properties.getClientId());
    client.put("clientVersion", properties.getClientVersion());
    requestBody.put("client", client);

    var threatInfos = new HashMap<String, Object>();
    threatInfos.put("threatTypes", properties.getThreatTypes());
    threatInfos.put("platformTypes", properties.getPlatformTypes());
    threatInfos.put("threatEntryTypes", properties.getEntryTypes());

    var threatEntries = urls.stream()
        .map(this::mappingUrl)
        .toList();

    threatInfos.put("threatEntries", threatEntries);
    requestBody.put("threatInfo", threatInfos);

    return requestBody;
  }

  private HashMap<String, String> mappingUrl(OriginalUrl url) {
    var entry = new HashMap<String, String>();
    entry.put("url", url.getValue());
    return entry;
  }
}
