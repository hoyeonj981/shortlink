package me.hoyeon.shortlink.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogJpaRepository extends JpaRepository<AccessLog, Long>, AccessLogQueryRepository {

  Long countByAlias(String alias);
}
