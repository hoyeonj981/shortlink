package me.hoyeon.shortlink.domain;

public interface UrlSafetyDecisionPolicy {

  SafetyDecision decide(UrlSafetyContext context);
}
