package me.hoyeon.shortlink.infrastructure.security;

import com.auth0.jwt.JWT;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationProvider(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    var token = (String) authentication.getCredentials();
    jwtTokenProvider.validate(token);
    var memberId = JWT.decode(token).getClaim("memberId");
    // role 구현필요
    return new JwtAuthenticationToken(memberId, token, null);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
