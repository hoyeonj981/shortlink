package me.hoyeon.shortlink.infrastructure;

import com.maxmind.geoip2.DatabaseProvider;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class IpGeoLocationProvider {

  private static final String IPV4_REEX =
      "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
      + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
      + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
      + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
  private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REEX);

  private final DatabaseProvider geoLite2DatabaseProvider;

  public IpGeoLocationProvider(DatabaseProvider geoLite2DatabaseProvider) {
    this.geoLite2DatabaseProvider = geoLite2DatabaseProvider;
  }

  public String extractCountry(String host) {
    validateIpv4(host);
    try {
      var ipAddress = InetAddress.getByName(host);
      var response = geoLite2DatabaseProvider.city(ipAddress);
      return response.getCountry().getIsoCode();
    } catch (GeoIp2Exception | UnknownHostException e) {
      return "N/A";
    } catch (IOException e) {
      throw new RuntimeException("IO 예외 발생", e);
    }
  }

  private void validateIpv4(String host) {
    if (!IPV4_PATTERN.matcher(host).matches()) {
      throw new IllegalArgumentException("유효하지 않는 IPv4 : " + host);
    }
  }
}
