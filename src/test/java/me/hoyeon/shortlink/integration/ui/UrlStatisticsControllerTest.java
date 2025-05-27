package me.hoyeon.shortlink.integration.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import me.hoyeon.shortlink.application.CountryStatisticDto;
import me.hoyeon.shortlink.application.DailyStatisticDto;
import me.hoyeon.shortlink.application.StatisticsService;
import me.hoyeon.shortlink.infrastructure.config.GlobalExceptionHandler;
import me.hoyeon.shortlink.ui.UrlStatisticsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UrlStatisticsControllerTest {

  private static final int DEFAULT_DAYS = 90;

  private StatisticsService statisticsService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    statisticsService = mock(StatisticsService.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new UrlStatisticsController(statisticsService))
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @DisplayName("일별 통계")
  @Nested
  class DailyStatistics {

    @DisplayName("GET /api/v1/statistics/{alias}/daily - 일별 통계 반환")
    @Test
    void getDailyStats_success() throws Exception {
      var response = List.of(
          new DailyStatisticDto(LocalDate.of(2025, 6, 10), 20L),
          new DailyStatisticDto(LocalDate.of(2025, 6, 11), 30L)
      );
      var alias = "alias";
      when(statisticsService.getDailyStatistics(anyString(), any(), any(), anyInt(), anyInt()))
          .thenReturn(response);

      mockMvc.perform(get("/api/v1/statistics/{alias}/daily", alias)
            .param("from", "2025-06-10")
            .param("to", "2025-06-11")
            .param("page", "0")
            .param("limit", "20")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].date").value("2025-06-10"))
          .andExpect(jsonPath("$[0].count").value(20L));
    }

    @DisplayName("GET /api/v1/statistics/{alias}/daily - 기간 오류로 예외 발생")
    @Test
    void getDailyStats_periodTooLong() throws Exception {
      var alias = "alias";

      mockMvc.perform(get("/api/v1/statistics/{alias}/daily", alias)
            .param("from", "2025-05-01")
            .param("to", "2025-01-01")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /api/v1/statistics/{alias}/daily - 최대 기한 초과로 예외 발생")
    @Test
    void getDailyStats_excessMaximumPeriod() throws Exception {
      var alias = "alias";
      var from = LocalDate.of(2025, 1, 1);
      var to = from.plusDays(DEFAULT_DAYS + 1);

      mockMvc.perform(get("/api/v1/statistics/{alias}/daily", alias)
            .param("from", from.toString())
            .param("to", to.toString())
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  @DisplayName("국가별 통계")
  @Nested
  class CountryStatistics {

    @DisplayName("GET /api/v1/statistics/{alias}/country - 국가별 통계 반환")
    @Test
    void getCountryStats_success() throws Exception {
      var alias = "alias";
      var response = List.of(
          new CountryStatisticDto("KR", 30L),
          new CountryStatisticDto("US", 12L)
      );
      when(statisticsService.getCountryStatistics(anyString(), any(), any(), anyInt(), anyInt()))
          .thenReturn(response);

      mockMvc.perform(get("/api/v1/statistics/{alias}/country", alias)
              .param("from", "2025-06-10")
              .param("to", "2025-06-11")
              .param("page", "0")
              .param("limit", "20")
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].countryCode").value("KR"))
          .andExpect(jsonPath("$[0].count").value(30L))
          .andExpect(jsonPath("$[1].countryCode").value("US"))
          .andExpect(jsonPath("$[1].count").value(12L));
    }

    @DisplayName("GET /api/v1/statistics/{alias}/country - 기간 오류로 예외 발생")
    @Test
    void getCountryStats_periodTooLong() throws Exception {
      var alias = "alias";

      mockMvc.perform(get("/api/v1/statistics/{alias}/country", alias)
            .param("from", "2025-06-10")
            .param("to", "2025-01-11")
            .param("page", "0")
            .param("limit", "20")
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /api/v1/statistics/{alias}/country - 최대 기한 초과로 예외 발생")
    @Test
    void getCountryStats_excessMaximumPeriod() throws Exception {
      var alias = "alias";
      var from = LocalDate.of(2025, 1, 1);
      var to = from.plusDays(DEFAULT_DAYS + 1);

      mockMvc.perform(get("/api/v1/statistics/{alias}/country", alias)
            .param("from", from.toString())
            .param("to", to.toString())
            .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }
}
