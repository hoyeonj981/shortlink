package me.hoyeon.shortlink.application;

public interface OAuthCredentialRepository {

  void save(Long memberId, String email, String provider);
}
