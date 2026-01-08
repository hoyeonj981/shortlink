package me.hoyeon.shortlink.unit.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.maxmind.geoip2.DatabaseProvider;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import java.io.IOException;
import java.net.InetAddress;
import me.hoyeon.shortlink.infrastructure.IpGeoLocationProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IpGeoLocationProviderTest {

  private static final String TEST_IP = "192.167.127.12";
  private static final String COUNTRY = "KR";

  @Mock
  private DatabaseProvider databaseProvider;

  @InjectMocks
  private IpGeoLocationProvider ipGeoLocationProvider;

  @DisplayName("주어진 IP 정보에서 국가의 ISO 코드를 추출한다")
  @Test
  void extractIsoFromGivenIp() throws Exception {
    var cityResponse = mock(CityResponse.class);
    var country = mock(Country.class);
    when(databaseProvider.city(any())).thenReturn(cityResponse);
    when(cityResponse.getCountry()).thenReturn(country);
    when(country.getIsoCode()).thenReturn(COUNTRY);

    var result = ipGeoLocationProvider.extractCountry(TEST_IP);

    assertThat(result).isEqualTo(COUNTRY);
  }

  @DisplayName("주어진 IP가 private IP인 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenIpIsPrivate() throws Exception {
    var privateIp = "10.0.0.1";
    var ipAddress = InetAddress.getByName(privateIp);
    when(databaseProvider.city(ipAddress)).thenThrow(GeoIp2Exception.class);

    var result = ipGeoLocationProvider.extractCountry(privateIp);

    assertThat(result).isEqualTo("N/A");
  }

  @DisplayName("주어진 IP가 reserved IP인 경우 예외가 발생핸다")
  @Test
  void throwExceptionWhenIpIsReserved() throws Exception {
    var reservedIp = "127.0.0.0";
    var ipAddress = InetAddress.getByName(reservedIp);
    when(databaseProvider.city(ipAddress)).thenThrow(GeoIp2Exception.class);

    var result = ipGeoLocationProvider.extractCountry(reservedIp);

    assertThat(result).isEqualTo("N/A");
  }

  @DisplayName("주어진 IP가 IPv4 형식이 아닐 경우 예외가 발생한다")
  @Test
  void throwExceptionWhenIpIsNotIpv4() throws Exception {
    var notIpv4 = "192.168.1.1.1";

    assertThatThrownBy(() -> ipGeoLocationProvider.extractCountry(notIpv4))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("IO 에러 시 예외가 발생한다")
  @Test
  void throwExceptionWhenIoErrorHasBeenHappened() throws Exception {
    when(databaseProvider.city(any())).thenThrow(IOException.class);

    assertThatThrownBy(() -> ipGeoLocationProvider.extractCountry(TEST_IP))
        .isInstanceOf(RuntimeException.class);
  }
}