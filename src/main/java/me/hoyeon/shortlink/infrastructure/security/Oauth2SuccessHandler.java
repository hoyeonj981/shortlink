package me.hoyeon.shortlink.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.application.MemberQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberQueryService memberQueryService;

  public Oauth2SuccessHandler(
      JwtTokenProvider jwtTokenProvider,
      MemberQueryService memberQueryService
  ) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.memberQueryService = memberQueryService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    var principal = (DefaultOAuth2User) authentication.getPrincipal();
    var email = (String) principal.getAttribute("email");
    var member = memberQueryService.getMemberByEmail(email);
    var accessToken = jwtTokenProvider.generateAccessToken(member);
    var refreshToken = jwtTokenProvider.generateRefreshToken(member);
    var  tokens = new HashMap<String, String>();
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);

    response.setContentType("application/json; charset=UTF-8");
    response.getWriter().write(new ObjectMapper().writeValueAsString(tokens));
    response.getWriter().flush();
  }
}
