package me.hoyeon.shortlink.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import me.hoyeon.shortlink.domain.DecisionThresholds;
import me.hoyeon.shortlink.domain.DecisionType;
import me.hoyeon.shortlink.domain.DecisionWeights;
import me.hoyeon.shortlink.domain.SafetyDecision;
import me.hoyeon.shortlink.domain.UrlRiskScore;
import me.hoyeon.shortlink.domain.UrlSafetyAssessor;
import me.hoyeon.shortlink.domain.UrlSafetyContext;
import me.hoyeon.shortlink.domain.WeightedUrlSafetyDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WeightedUrlSafetyDecisionTest {

  private UrlSafetyAssessor assessor;
  private WeightedUrlSafetyDecision policy;

  @BeforeEach
  void setUp() {
    assessor = mock(UrlSafetyAssessor.class);
    // 가중치: 기술 1.0, 컨텐츠 1.0, 행위 1.0
    var weights = new DecisionWeights(1.0, 1.0, 1.0);
    // 임계값: Block 80.0, Warn 50.0
    var thresholds = new DecisionThresholds(80.0, 50.0);

    policy = new WeightedUrlSafetyDecision(assessor, weights, thresholds);
  }

  @DisplayName("가중치 합산 점수가 차단 임계값 이상이면 BLOCK을 반환해야 한다")
  @Test
  void decide_shouldReturnBlock_whenScoreExceedsBlockThreshold() {
    // Total = 30 + 30 + 30 = 90 (>= 80)
    var score = new UrlRiskScore(30.0, 30.0, 30.0);
    given(assessor.assess(any(UrlSafetyContext.class))).willReturn(score);
    var context = mock(UrlSafetyContext.class);

    var decision = policy.decide(context);

    assertThat(decision.getDecisionType()).isEqualTo(DecisionType.BLOCK);
    assertThat(decision.getTotalScore()).isEqualTo(90.0);
  }

  @DisplayName("가중치 합산 점수가 경고 임계값 이상이고 차단 임계값 미만이면 WARN을 반환해야 한다")
  @Test
  void decide_shouldReturnWarn_whenScoreExceedsWarnThreshold() {
    // Total = 20 + 20 + 20 = 60 (>= 50 and < 80)
    var score = new UrlRiskScore(20.0, 20.0, 20.0);
    given(assessor.assess(any(UrlSafetyContext.class))).willReturn(score);
    var context = mock(UrlSafetyContext.class);

    var decision = policy.decide(context);

    assertThat(decision.getDecisionType()).isEqualTo(DecisionType.WARN);
    assertThat(decision.getTotalScore()).isEqualTo(60.0);
  }

  @DisplayName("가중치 합산 점수가 경고 임계값 미만이면 ALLOW를 반환해야 한다")
  @Test
  void decide_shouldReturnAllow_whenScoreIsLow() {
    // Total = 10 + 10 + 10 = 30 (< 50)
    var score = new UrlRiskScore(10.0, 10.0, 10.0);
    given(assessor.assess(any(UrlSafetyContext.class))).willReturn(score);
    var context = mock(UrlSafetyContext.class);

    var decision = policy.decide(context);

    assertThat(decision.getDecisionType()).isEqualTo(DecisionType.ALLOW);
    assertThat(decision.getTotalScore()).isEqualTo(30.0);
  }

  @DisplayName("가중치가 적용된 점수로 판단해야 한다")
  @Test
  void decide_shouldApplyWeightsCorrectly() {
    // Total = 100 >= 100
    // 가중치 변경: 기술 2.0, 컨텐츠 0.5, 행위 1.0
    var weights = new DecisionWeights(2.0, 0.5, 1.0);
    var thresholds = new DecisionThresholds(100.0, 50.0);
    policy = new WeightedUrlSafetyDecision(assessor, weights, thresholds);
    // Weighted Score: (30 * 2.0) + (40 * 0.5) + (20 * 1.0) = 60 + 20 + 20 = 100
    var score = new UrlRiskScore(30.0, 40.0, 20.0);
    given(assessor.assess(any(UrlSafetyContext.class))).willReturn(score);

    var context = mock(UrlSafetyContext.class);
    var decision = policy.decide(context);

    assertThat(decision.getTotalScore()).isEqualTo(100.0);
    assertThat(decision.getDecisionType()).isEqualTo(DecisionType.BLOCK);
  }
}
