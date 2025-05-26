package me.hoyeon.shortlink.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import me.hoyeon.shortlink.application.CountryStatisticDto;
import me.hoyeon.shortlink.application.DailyStatisticDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogJpaRepository extends JpaRepository<AccessLog, Long> {

  Long countByAlias(String alias);

  @Query("")
  List<DailyStatisticDto> findDailyStatByAlias(
      @Param("alias") String alias,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to
  );

  @Query("")
  List<CountryStatisticDto> findCountryStatByAlias(
      @Param("alias") String alias,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      int limit,
      Pageable pageable
  );
}
