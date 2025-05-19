package me.hoyeon.shortlink.infrastructure.config;

import java.util.List;

public class GoogleUrlSafetyProperties {

  private String clientId;
  private String clientVersion;
  private String apiKey;
  private String endpoint;
  private List<String> threatTypes;
  private List<String> platformTypes;
  private List<String> entryTypes;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientVersion() {
    return clientVersion;
  }

  public void setClientVersion(String clientVersion) {
    this.clientVersion = clientVersion;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public List<String> getThreatTypes() {
    return threatTypes;
  }

  public void setThreatTypes(List<String> threatTypes) {
    this.threatTypes = threatTypes;
  }

  public List<String> getPlatformTypes() {
    return platformTypes;
  }

  public void setPlatformTypes(List<String> platformTypes) {
    this.platformTypes = platformTypes;
  }

  public List<String> getEntryTypes() {
    return entryTypes;
  }

  public void setEntryTypes(List<String> entryTypes) {
    this.entryTypes = entryTypes;
  }
}
