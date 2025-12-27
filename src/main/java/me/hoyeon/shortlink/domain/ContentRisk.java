package me.hoyeon.shortlink.domain;

import java.util.Map;

/*
* URL 컨텐츠 위험 요소
* 예시 : 도박, 폭력, 성인물 등
 */
public record ContentRisk(
    Map<ContentRiskCategory, RiskLevel> factors
) {}
