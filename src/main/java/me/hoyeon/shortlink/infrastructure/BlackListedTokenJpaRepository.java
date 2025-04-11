package me.hoyeon.shortlink.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListedTokenJpaRepository extends JpaRepository<BlackListedToken, Long> {

  BlackListedToken save(BlackListedToken blackListedToken);

  boolean existsByToken(String token);
}
