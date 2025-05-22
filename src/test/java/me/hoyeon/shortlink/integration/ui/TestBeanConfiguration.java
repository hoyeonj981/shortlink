package me.hoyeon.shortlink.integration.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hoyeon.shortlink.application.AuthenticationService;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.application.MemberQueryService;
import me.hoyeon.shortlink.application.MemberRegistrationService;
import me.hoyeon.shortlink.infrastructure.config.OauthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

public class TestBeanConfiguration {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  OauthProperties oauthProperties;

  @MockitoBean
  AuthenticationService authenticationService;

  @MockitoBean
  MemberQueryService memberQueryService;

  @MockitoBean
  MemberRegistrationService memberRegistrationService;

  @MockitoBean
  JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  ClientRegistrationRepository clientRegistrationRepository;
}
