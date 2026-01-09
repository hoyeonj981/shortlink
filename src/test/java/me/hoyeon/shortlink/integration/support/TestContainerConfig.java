package me.hoyeon.shortlink.integration.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfig {

  private static final String MYSQL_VERSION = "mysql:8.4.0";

  @Bean
  @ServiceConnection
  public MySQLContainer<?> mysqlContainer() {
    return new MySQLContainer<>(DockerImageName.parse(MYSQL_VERSION));
  }
}
