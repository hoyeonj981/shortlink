package me.hoyeon.shortlink.infrastructure.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OauthProperties {

  private static final String REGISTRATION = "spring.security.oauth2.client.registration.";
  private static final String CLIENT_ID = ".client-id";
  private static final String SPRING_SECURITY_OAUTH2_DEFAULT_URL = "/oauth2/authorization/";

  private final Environment env;

  public OauthProperties(Environment env) {
    this.env = env;
  }

  public String getOauthAuthorizeUrl(String provider) {
    if (!isEnabled(provider)) {
      throw new IllegalArgumentException("Not Supported Provider");
    }
    return SPRING_SECURITY_OAUTH2_DEFAULT_URL + provider;
  }

  private boolean isEnabled(String provider) {
    return env.getProperty(REGISTRATION + provider + CLIENT_ID) != null;
  }
}
