package me.hoyeon.shortlink.unit.application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import me.hoyeon.shortlink.application.AccessLogWriter;
import me.hoyeon.shortlink.application.RedirectInfo;
import me.hoyeon.shortlink.application.RedirectLoggingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RedirectLoggingServiceTest {

  @Mock
  private AccessLogWriter accessLogWriter;

  @InjectMocks
  private RedirectLoggingService redirectLoggingService;

  @DisplayName("log 메서드는 AccessLogWriter의 write 메서드를 호출한다")
  @Test
  void logShouldCallWriteMethodOfAccessLogWriter() {
    var alias = "abcdef";
    var originalUrl = "https://www.example.com";
    var givenIp = "127.0.0.1";
    var givenUserAgent = "test-user-agent";
    var givenReferer = "https://www.google.com";
    var testInfo = new RedirectInfo(
        alias, originalUrl, givenIp, givenUserAgent, givenReferer
    );

    redirectLoggingService.log(testInfo);

    verify(accessLogWriter, times(1)).write(testInfo);
  }
}