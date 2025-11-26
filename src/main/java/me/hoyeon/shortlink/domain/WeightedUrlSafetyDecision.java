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
    return null;
  }
}
