package me.hoyeon.shortlink.integration.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import me.hoyeon.shortlink.application.RedirectInfo;
import me.hoyeon.shortlink.infrastructure.AccessLogDbRepository;
import me.hoyeon.shortlink.infrastructure.AccessLogJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AccessLogDbRepositoryTest {

  @Autowired
  private AccessLogJpaRepository accessLogJpaRepository;

  private AccessLogDbRepository accessLogDbRepository;

  @BeforeEach
  void setUp() {
    accessLogDbRepository = new AccessLogDbRepository(accessLogJpaRepository);
  }

  @DisplayName("리다이렉트 정보를 받으면 접근 로그를 저장한다")
  @Test
  void writeAccessLogWhenRedirectInfoIsGiven() {
    var alias = "testAlias";
    var originalUrl = "http://example.com";
    var userAgent = "test-agent";
    var ip = "127.0.0.1";
    var referer = "http://referer.com";
    var redirectInfo = new RedirectInfo(alias, originalUrl, ip, userAgent, referer);

    accessLogDbRepository.write(redirectInfo);

    var foundLogs = accessLogJpaRepository.findAll();
    assertThat(foundLogs).hasSize(1);
    var savedLog = foundLogs.get(0);
    assertThat(savedLog.getAlias()).isEqualTo(alias);
    assertThat(savedLog.getOriginalUrl()).isEqualTo(originalUrl);
    assertThat(savedLog.getUserAgent()).isEqualTo(userAgent);
    assertThat(savedLog.getIp()).isEqualTo(ip);
    assertThat(savedLog.getReferer()).isEqualTo(referer);
  }

  @DisplayName("주어진 별명(alias)에 해당하는 접근 로그의 총 개수를 반환한다")
  @Test
  void returnTotalCountForGivenAlias() {
    var alias1 = "alias1";
    var alias2 = "alias2";
    var redirectInfo1 = new RedirectInfo(alias1, "url1", "127.0.0.1", "test-agent", "ref");
    var redirectInfo2 = new RedirectInfo(alias1, "url2", "127.0.0.1", "test-agent", "ref");
    var redirectInfo3 = new RedirectInfo(alias2, "url3", "127.0.0.1", "test-agent", "ref");
    accessLogDbRepository.write(redirectInfo1);
    accessLogDbRepository.write(redirectInfo2);
    accessLogDbRepository.write(redirectInfo3);

    long countForAlias1 = accessLogDbRepository.getTotalCount(alias1);
    long countForAlias2 = accessLogDbRepository.getTotalCount(alias2);
    long countForNonExistentAlias = accessLogDbRepository.getTotalCount("non-existent");

    assertThat(countForAlias1).isEqualTo(2);
    assertThat(countForAlias2).isEqualTo(1);
    assertThat(countForNonExistentAlias).isEqualTo(0);
  }
}
