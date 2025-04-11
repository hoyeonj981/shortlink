package me.hoyeon.shortlink.integration.infrastructure;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

  @Bean
  public Clock clock() {
    return Clock.fixed(
        Instant.parse("2023-01-01T12:00:00Z"),
        ZoneId.systemDefault()
    );
  }
}
