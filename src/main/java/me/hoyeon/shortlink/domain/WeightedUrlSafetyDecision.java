package me.hoyeon.shortlink.domain;

public class WeightedUrlSafetyDecision implements UrlSafetyDecisionPolicy {

  private final UrlSafetyAssessor assessor;
  private final DecisionWeights weights;
  private final DecisionThresholds thresholds;

  public WeightedUrlSafetyDecision(UrlSafetyAssessor assessor, DecisionWeights weights,
      DecisionThresholds thresholds) {
    this.assessor = assessor;
    this.weights = weights;
    this.thresholds = thresholds;
  }

  @Override
  public SafetyDecision decide(UrlSafetyContext context) {
    var score = assessor.assess(context);
    var weighted = score.technical() * weights.technicalWeight()
        + score.content() * weights.contentWeight()
        + score.behavior() * weights.behaviorWeight();

    if (weighted >= thresholds.blockThreshold()) {
      return SafetyDecision.block(weighted, score, "block");
    }
    if (weighted >= thresholds.warnThreshold()) {
      return SafetyDecision.warn(weighted, score, "warn");
    }
    return SafetyDecision.allow(weighted, score, "allow");
  }
}
