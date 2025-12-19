package me.hoyeon.shortlink.domain;

import java.util.Map;

/*
* 사용자 행위 기반 위험 요소
* 예시 : 짧은 시간에 많은 요청을 할 경우, 같은 도메인 주소로 반복적인 작업을 지속할 경우 등
 */
public record BehaviorRisk(
    Map<BehaviorRiskCategory, RiskLevel> factors
) { }
