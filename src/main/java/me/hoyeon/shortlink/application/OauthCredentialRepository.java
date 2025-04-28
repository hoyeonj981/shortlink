package me.hoyeon.shortlink.application;

public interface OauthCredentialRepository {

  void save(Long memberId, String email, String provider);
}
