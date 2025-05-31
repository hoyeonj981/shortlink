package me.hoyeon.shortlink.application;

public class RedirectLoggingService {

  private final AccessLogWriter accessLogWriter;

  public RedirectLoggingService(AccessLogWriter accessLogWriter) {
    this.accessLogWriter = accessLogWriter;
  }

  public void log(RedirectInfo info) {
    accessLogWriter.write(info);
  }
}
