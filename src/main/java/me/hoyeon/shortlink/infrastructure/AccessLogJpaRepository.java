package me.hoyeon.shortlink.infrastructure;

import java.time.LocalDate;
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

  @Query("SELECT new me.hoyeon.shortlink.application.DailyStatisticDto("
      + "DATE(a.redirectedAt), COUNT(a)) "
      + "FROM AccessLog a "
      + "WHERE a.alias = :alias AND a.redirectedAt BETWEEN :from AND :to "
      + "GROUP BY DATE(a.redirectedAt) "
      + "ORDER BY DATE(a.redirectedAt) ASC")
  List<DailyStatisticDto> findDailyStatByAlias(
      @Param("alias") String alias,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      Pageable pageable
  );

  @Query("SELECT new me.hoyeon.shortlink.application.CountryStatisticDto("
      + "a.country, COUNT(a)) "
      + "FROM AccessLog a "
      + "WHERE a.alias = :alias AND a.redirectedAt BETWEEN :from AND :to "
      + "GROUP BY a.country "
      + "ORDER BY COUNT(a) DESC")
  List<CountryStatisticDto> findCountryStatByAlias(
      @Param("alias") String alias,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      Pageable pageable
  );
}
