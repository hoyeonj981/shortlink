package me.hoyeon.shortlink.domain;

public enum BehaviorRiskCategory {

  /*
  * 동일한 IP에서 단시간 내에 많은 단축 URL을 생성하는 행위
  * 기준 : 60초 동안 생성된 단축 URL이 100개를 넘는 경우
   */
  HIGH_FREQUENCY_CREATION,

  /*
  * 악성 봇, 비정상적인 User-Agent
   */
  SUSPICIOUS_USER_AGENT,

  /*
  * 이미 블록된 IP
   */
  BLOCKED_IP,
  ;
}
