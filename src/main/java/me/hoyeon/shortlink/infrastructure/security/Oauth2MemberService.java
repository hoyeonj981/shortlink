package me.hoyeon.shortlink.infrastructure.security;

import java.util.Collections;
import me.hoyeon.shortlink.application.MemberNotFoundException;
import me.hoyeon.shortlink.application.MemberQueryService;
import me.hoyeon.shortlink.domain.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class Oauth2MemberService extends DefaultOAuth2UserService {

  private static final String ROLE_VERIFIED = "ROLE_VERIFIED";
  private static final String ROLE_UNVERIFIED = "ROLE_UNVERIFIED";

  private final MemberQueryService memberQueryService;

  public Oauth2MemberService(MemberQueryService memberQueryService) {
    this.memberQueryService = memberQueryService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    var oauth2User = super.loadUser(userRequest);
    var emailAddress = (String) oauth2User.getAttribute("email");

    try {
      var member = memberQueryService.getMemberByEmail(emailAddress);
      return convert(oauth2User, member);
    } catch (MemberNotFoundException e) {
      throw new OAuth2AuthenticationException(new OAuth2Error(
          "oauth_user_not_found"),
          "회원가입이 필요합니다");
    }
  }

  private OAuth2User convert(OAuth2User oauth2User, Member member) {
    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(getAuthority(member))),
        oauth2User.getAttributes(),
        "email");
  }

  private String getAuthority(Member member) {
    if (member.isVerified()) {
      return ROLE_VERIFIED;
    } else {
      return ROLE_UNVERIFIED;
    }
  }
}
