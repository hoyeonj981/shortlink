package me.hoyeon.shortlink.domain;

import java.util.Map;

public class UrlSafetyAssessor {

  private static final Map<RiskLevel, Double> RISK_LEVEL_SCORES;

  /*
   * RiskLevel을 가져오고 해당 enum에 맞는 값을 하드코딩으로 넣기
   * 일단 단순하게 구현. 나중에 필요할 경우 각 Risk에 맞게 분류할 수 있어야 함
   */
  static {
    RISK_LEVEL_SCORES = Map.of(
        RiskLevel.NEGLIGIBLE, 0.0,
        RiskLevel.LOW, 1.0,
        RiskLevel.MEDIUM, 5.0,
        RiskLevel.HIGH, 10.0,
        RiskLevel.CRITICL, 20.0
    );
  }

  public UrlRiskScore assess(UrlSafetyContext context) {
    var technicalRisk = context.getTechnicalRisk();
    var contentRisk = context.getContentRisk();
    var behaviorRisk = context.getBehaviorRisk();
    return new UrlRiskScore(
        assessTechnicalRisk(technicalRisk),
        assessContentRisk(contentRisk),
        assessBehaviorRisk(behaviorRisk)
    );
  }

  private double assessTechnicalRisk(TechnicalRisk technicalRisk) {
    return technicalRisk.factors()
        .values()
        .stream()
        .mapToDouble(RISK_LEVEL_SCORES::get)
        .sum();
  }

  private double assessContentRisk(ContentRisk contentRisk) {
    return contentRisk.factors()
        .values()
        .stream()
        .mapToDouble(RISK_LEVEL_SCORES::get)
        .sum();
  }

  private double assessBehaviorRisk(BehaviorRisk behaviorRisk) {
    return behaviorRisk.factors()
        .values()
        .stream()
        .mapToDouble(RISK_LEVEL_SCORES::get)
        .sum();
  }
}
