package me.hoyeon.shortlink.domain;

import java.util.Map;

/*
* 기술 관련 위험 요소
* 예시 : 스미싱, 악성 코드, 멀웨어 등
 */
public record TechnicalRisk(
    Map<TechnicalRiskCategory, RiskLevel> factors
) {}
