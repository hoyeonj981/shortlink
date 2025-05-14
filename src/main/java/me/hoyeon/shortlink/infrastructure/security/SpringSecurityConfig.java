package me.hoyeon.shortlink.infrastructure.security;

import me.hoyeon.shortlink.application.JwtTokenProvider;
import me.hoyeon.shortlink.application.MemberQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

  private final MemberQueryService memberQueryService;
  private final JwtTokenProvider jwtTokenProvider;

  public SpringSecurityConfig(
      MemberQueryService memberQueryService,
      JwtTokenProvider jwtTokenProvider
  ) {
    this.memberQueryService = memberQueryService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/**").permitAll()) // 임시 설정. 배포 단계에서는 수정
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(oauth2MemberService()))
            .successHandler(oauth2SuccessHandler())
            .failureHandler(oauth2FailureHandler()));

    http.authenticationProvider(jwtAuthenticationProvider())
        .addFilterBefore(
            jwtAuthenticationFilter(http.getSharedObject(AuthenticationManager.class)),
            UsernamePasswordAuthenticationFilter.class);

    // h2 console
    http.headers(headers -> headers
        .frameOptions(FrameOptionsConfig::disable));

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }


  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider() {
    return new JwtAuthenticationProvider(jwtTokenProvider);
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      AuthenticationManager authenticationManager
  ) {
    return new JwtAuthenticationFilter(authenticationManager);
  }

  @Bean
  public Oauth2MemberService oauth2MemberService() {
    return new Oauth2MemberService(memberQueryService);
  }

  @Bean
  public Oauth2SuccessHandler oauth2SuccessHandler() {
    return new Oauth2SuccessHandler(jwtTokenProvider, memberQueryService);
  }

  @Bean
  public Oauth2FailureHandler oauth2FailureHandler() {
    return new Oauth2FailureHandler();
  }

  @Bean
  public PasswordEncoder securityPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
