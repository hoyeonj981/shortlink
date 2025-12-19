package me.hoyeon.shortlink.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import me.hoyeon.shortlink.domain.BehaviorRisk;
import me.hoyeon.shortlink.domain.BehaviorRiskCategory;
import me.hoyeon.shortlink.domain.ContentRisk;
import me.hoyeon.shortlink.domain.ContentRiskCategory;
import me.hoyeon.shortlink.domain.RiskLevel;
import me.hoyeon.shortlink.domain.TechnicalRisk;
import me.hoyeon.shortlink.domain.TechnicalRiskCategory;
import me.hoyeon.shortlink.domain.UrlSafetyAssessor;
import me.hoyeon.shortlink.domain.UrlSafetyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlSafetyAssessorTest {

  private final UrlSafetyAssessor assessor = new UrlSafetyAssessor();

  @DisplayName("URL 안전성 평가 시 각 위험 요소별 점수를 정확히 계산해야 한다")
  @Test
  void assess_shouldCalculateScoresCorrectly() {
    var technicalRisk = new TechnicalRisk(Map.of(
        TechnicalRiskCategory.MALWARE_DISTRIBUTION, RiskLevel.CRITICL, // 20.0
        TechnicalRiskCategory.DECEPTIVE_CONTENT, RiskLevel.HIGH // 10.0
    ));

    var contentRisk = new ContentRisk(Map.of(
        ContentRiskCategory.R18, RiskLevel.MEDIUM, // 5.0
        ContentRiskCategory.GAMBLING, RiskLevel.LOW // 1.0
    )); // Total Content: 6.0

    var behaviorRisk = new BehaviorRisk(Map.of(
        BehaviorRiskCategory.HIGH_FREQUENCY_CREATION, RiskLevel.NEGLIGIBLE // 0.0
    )); // Total Behavior: 0.0

    var context = new UrlSafetyContext(
        null,
        null,
        technicalRisk,
        contentRisk,
        behaviorRisk
    );
    var score = assessor.assess(context);

    assertThat(score.technical()).isEqualTo(30.0);
    assertThat(score.content()).isEqualTo(6.0);
    assertThat(score.behavior()).isEqualTo(0.0);
  }

  @DisplayName("위험 요소가 없는 경우 점수는 0이어야 한다")
  @Test
  void assess_shouldReturnZero_whenNoRisks() {
    var technicalRisk = new TechnicalRisk(Map.of());
    var contentRisk = new ContentRisk(Map.of());
    var behaviorRisk = new BehaviorRisk(Map.of());

    var context = new UrlSafetyContext(null, null, technicalRisk, contentRisk,
        behaviorRisk);
    var score = assessor.assess(context);

    assertThat(score.technical()).isEqualTo(0.0);
    assertThat(score.content()).isEqualTo(0.0);
    assertThat(score.behavior()).isEqualTo(0.0);
  }
}
