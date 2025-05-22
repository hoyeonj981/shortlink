package me.hoyeon.shortlink.ui;

import me.hoyeon.shortlink.application.AuthenticationService;
import me.hoyeon.shortlink.application.MemberRegistrationService;
import me.hoyeon.shortlink.application.SignInResponse;
import me.hoyeon.shortlink.infrastructure.config.OauthProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationService authenticationService;
  private final MemberRegistrationService memberRegistrationService;
  private final OauthProperties oauthProperties;

  public AuthController(
      AuthenticationService authenticationService,
      MemberRegistrationService memberRegistrationService,
      OauthProperties oauthProperties
  ) {
    this.authenticationService = authenticationService;
    this.memberRegistrationService = memberRegistrationService;
    this.oauthProperties = oauthProperties;
  }

  @PostMapping("/login")
  public ResponseEntity<SignInResponse> login(@RequestBody SignInRequest request) {
    var signInResponse = authenticationService.signIn(request.email(), request.rawPassword());
    return ResponseEntity.ok(signInResponse);
  }

  @PostMapping("/signup")
  public ResponseEntity<MemberResponse> signUp(@RequestBody SignUpRequest request) {
    var member = memberRegistrationService.registerWithEmail(
        request.email(), request.rawPassword());
    var memberResponse = new MemberResponse(member.getId(), member.getEmail().getAddress());
    return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
  }

  @GetMapping("/login/oauth2/{provider}")
  public ResponseEntity<Void> getOauthUrl(@PathVariable String provider) {
    var oauthAuthorizeUrl = oauthProperties.getOauthAuthorizeUrl(provider);
    return ResponseEntity.status(HttpStatus.FOUND)
        .header("Location", oauthAuthorizeUrl)
        .build();
  }
}
