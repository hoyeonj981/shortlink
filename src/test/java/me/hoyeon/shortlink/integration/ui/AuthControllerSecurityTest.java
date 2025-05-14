package me.hoyeon.shortlink.integration.ui;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.hoyeon.shortlink.application.MemberNotFoundException;
import me.hoyeon.shortlink.application.MismatchPasswordException;
import me.hoyeon.shortlink.application.SignInResponse;
import me.hoyeon.shortlink.infrastructure.config.NotSupportedProviderException;
import me.hoyeon.shortlink.infrastructure.security.SpringSecurityConfig;
import me.hoyeon.shortlink.ui.AuthController;
import me.hoyeon.shortlink.ui.SignInRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@WebMvcTest(AuthController.class)
@Import(SpringSecurityConfig.class)
public class AuthControllerSecurityTest extends TestBeanConfiguration {

  private static final String EMAIL = "test@example.com";
  private static final String PASSWORD = "password1234!@";
  private static final String ACCESS_TOKEN = "test-access-token";
  private static final String REFRESH_TOKEN = "test-refresh-token";

  @DisplayName("이메일 기반 로그인")
  @Nested
  class EmailBasedLoginTest {

    @DisplayName("POST /api/v1/auth/login - 정상 로그인 후 토큰발행을 발행한다")
    @Test
    void loginSuccessfullyAndIssueTokens() throws Exception {
      var request = new SignInRequest(EMAIL, PASSWORD);
      var response = new SignInResponse(ACCESS_TOKEN, REFRESH_TOKEN);
      when(authenticationService.signIn(EMAIL, PASSWORD)).thenReturn(response);

      mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
          .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

    @DisplayName("POST /api/v1/auth/login - 이메일이 존재하지 않으면 401를 반환한다")
    @Test
    void return402IfEmailDoesNotExist() throws Exception {
      var request = new SignInRequest(EMAIL, PASSWORD);
      when(authenticationService.signIn(EMAIL, PASSWORD)).thenThrow(MemberNotFoundException.class);

      mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnauthorized());
    }

    @DisplayName("POST /api/v1/auth/login - 비밀번호가 불일치하면 401를 반환한다")
    @Test
    void return402IfPasswordDoesNotMatch() throws Exception {
      var request = new SignInRequest(EMAIL, PASSWORD);
      when(authenticationService.signIn(EMAIL, PASSWORD))
          .thenThrow(MismatchPasswordException.class);

      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnauthorized());
    }

    @DisplayName("POST /api/v1/auth/login - 이메일이 공백일 경우 400를 반환한다")
    @Test
    void return400IfEmailIsBlank() throws Exception {
      var request = new SignInRequest(null, PASSWORD);

      mockMvc.perform(post("/api/v1/auth/login"))
          .andExpect(status().isBadRequest());
    }

    @DisplayName("POST /api/v1/auth/login - 비밀번호가 공백일 경우 400를 반환한다")
    @Test
    void return400IfPasswordIsBlank() throws Exception{
      var request = new SignInRequest(EMAIL, null);

      mockMvc.perform(post("/api/v1/auth/login"))
          .andExpect(status().isBadRequest());
    }
  }

  @DisplayName("Oauth 기반 로그인")
  @Nested
  class OauthLoginTest {

    @DisplayName("GET /login/oauth2/{provider} - 해당 provider의 리다이렉트 URL 요청한다")
    @Test
    void redirectToOauthProvider() throws Exception {
      var provider = "test-provider";
      var redirectUrl = "/oauth/authorization" + provider;
      when(oauthProperties.getOauthAuthorizeUrl(provider)).thenReturn(redirectUrl);

      mockMvc.perform(get("/api/v1/auth/login/oauth2/{provider}", provider))
          .andExpect(status().isFound())
          .andExpect(header().string("Location", redirectUrl));
    }

    @DisplayName("GET /login/oauth2/{provider} - 존재하지 않는 provider는 400을 반환한다")
    @Test
    void return400IfProviderDoesNotExist() throws Exception {
      var provider = "not-exist-provider";
      when(oauthProperties.getOauthAuthorizeUrl(provider))
          .thenThrow(NotSupportedProviderException.class);

      mockMvc.perform(get("/api/v1/auth/login/oauth2/{provider}", provider))
          .andExpect(status().isBadRequest());
    }
  }
}
