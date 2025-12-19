package me.hoyeon.shortlink.domain;

public class SafetyDecision {

  private final DecisionType decisionType;
  private final double totalScore;
  private final UrlRiskScore details;
  private final String reason;

  public static SafetyDecision allow(double weighted, UrlRiskScore details, String reason) {
    return new SafetyDecision(DecisionType.ALLOW, weighted, details, reason);
  }

  public static SafetyDecision warn(double weighted, UrlRiskScore details, String reason) {
    return new SafetyDecision(DecisionType.WARN, weighted, details, reason);
  }

  public static SafetyDecision block(double weighted, UrlRiskScore details, String reason) {
    return new SafetyDecision(DecisionType.BLOCK, weighted, details, reason);
  }

  private SafetyDecision(DecisionType decisionType, double totalScore, UrlRiskScore details,
      String reason) {
    this.decisionType = decisionType;
    this.totalScore = totalScore;
    this.details = details;
    this.reason = reason;
  }

  public DecisionType getDecisionType() {
    return decisionType;
  }

  public double getTotalScore() {
    return totalScore;
  }

  public UrlRiskScore getDetails() {
    return details;
  }

  public String getReason() {
    return reason;
  }
}
