package me.hoyeon.shortlink.infrastructure.security;

import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.MEMBER_ID;
import static me.hoyeon.shortlink.infrastructure.JwtClaimKey.ROLE;

import java.util.Collection;
import java.util.Collections;
import me.hoyeon.shortlink.application.ApplicationException;
import me.hoyeon.shortlink.application.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationProvider(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      var token = (String) authentication.getCredentials();
      jwtTokenProvider.validate(token);
      var memberId = jwtTokenProvider.getClaim(token, MEMBER_ID.getClaimName());
      var role = jwtTokenProvider.getClaim(token, ROLE.getClaimName());
      return new JwtAuthenticationToken(memberId, token, createAuthority(role));
    } catch (ApplicationException e) {
      throw new BadCredentialsException(e.getMessage(), e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private Collection<? extends GrantedAuthority> createAuthority(String role) {
    return Collections.singleton(new SimpleGrantedAuthority(role));
  }
}
