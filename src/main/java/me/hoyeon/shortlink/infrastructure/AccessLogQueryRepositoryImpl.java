package me.hoyeon.shortlink.infrastructure;

import static me.hoyeon.shortlink.infrastructure.QAccessLog.accessLog;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import me.hoyeon.shortlink.application.CountryStatisticDto;
import me.hoyeon.shortlink.application.DailyStatisticDto;
import org.springframework.data.domain.Pageable;

public class AccessLogQueryRepositoryImpl implements AccessLogQueryRepository {

  private final JPAQueryFactory queryFactory;

  public AccessLogQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<DailyStatisticDto> findDailyStatByAlias(
      String alias,
      LocalDate from,
      LocalDate to,
      Pageable pageable
  ) {
    return queryFactory
        .select(Projections.constructor(
            DailyStatisticDto.class,
            accessLog.redirectedAt,
            accessLog.count()))
        .from(accessLog)
        .where(
            accessLog.alias.eq(alias)
                .and(accessLog.redirectedAt.between(from.atStartOfDay(), to.atTime(23, 59, 59)))
        )
        .groupBy(accessLog.redirectedAt)
        .orderBy(accessLog.redirectedAt.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  @Override
  public List<CountryStatisticDto> findCountryStatByAlias(
      String alias,
      LocalDate from,
      LocalDate to,
      Pageable pageable
  ) {
    return queryFactory
        .select(Projections.constructor(
            CountryStatisticDto.class,
            accessLog.country,
            accessLog.count()))
        .from(accessLog)
        .where(
            accessLog.alias.eq(alias)
                .and(accessLog.redirectedAt.between(from.atStartOfDay(), to.atTime(23, 59, 59)))
        )
        .groupBy(accessLog.country)
        .orderBy(accessLog.country.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }
}
