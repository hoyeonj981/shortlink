package me.hoyeon.shortlink.domain;

public class UrlSafetyAssessor {

  public UrlRiskScore assess(UrlSafetyContext context) {
    var technicalRisk = context.getTechnicalRisk();
    var contentRisk = context.getContentRisk();
    var behaviorRisk = context.getBehaviorRisk();
    return null;
  }

  private double assessTechnicalRisk(TechnicalRisk technicalRisk) {
    return 0;
  }

  private double assessContentRisk(ContentRisk contentRisk) {
    return 0;
  }

  private double assessBehaviorRisk(BehaviorRisk behaviorRisk) {
    return 0;
  }
}
