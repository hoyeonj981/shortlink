package me.hoyeon.shortlink.domain;

public enum TechnicalRiskCategory {
  /**
   * 사회 공학 및 기만 행위
   * 예: 피싱, 스미싱 연동 URL
   */
  DECEPTIVE_CONTENT,

  /**
   * 악성 소프트웨어
   * 예: 랜섬웨어, 바이러스, 원치 않는 소프트웨어
   */
  MALWARE_DISTRIBUTION,

  /**
   * 서비스 내부 접근 시도
   * 예: localhost, 127.0.0.1, 클라우드 메타 데이터 접근
   */
  INFRASTRCUTURE_ABUSE,

  /**
   * 알려진 블랙리스트, 신뢰할 수 없는 출처
   * 예: 스팸 도메인에 등록, 도메인 생성 일자가 매우 최근
   */
  POOR_REPUTATION,

  /**
   * 프로토콜 규격 위반 시도
   * 예: URL에 개행 문자, 원치않는 스키마
   */
  PROTOCOL_VIOLATION,

  /**
   * 의심스러운 URL 구조
   * 예: 비정상적인 문자, URL 패턴의 길이
   */
  SUSPICIOUS_STRCUTRUE,
  ;
}
