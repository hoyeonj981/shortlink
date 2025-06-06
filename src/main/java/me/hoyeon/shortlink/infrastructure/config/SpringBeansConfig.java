package me.hoyeon.shortlink.infrastructure.config;

import com.maxmind.geoip2.DatabaseProvider;
import com.maxmind.geoip2.DatabaseReader;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeansConfig {

  @PersistenceContext
  private EntityManager entityManager;

  @Value("maxMind.geoLite2.databasePath")
  private String geoLite2DatabasePath;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(entityManager);
  }

  @Bean
  public DatabaseProvider databaseProvider() throws IOException {
    return new DatabaseReader.Builder(new File(geoLite2DatabasePath)).build();
  }
}
