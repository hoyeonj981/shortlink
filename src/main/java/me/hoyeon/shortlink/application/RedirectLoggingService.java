package me.hoyeon.shortlink.application;

public class RedirectLoggingService {

  private final AccessLogWriter accessLogWriter;
  private final AccessLogReader accessLogReader;

  public RedirectLoggingService(AccessLogWriter accessLogWriter, AccessLogReader accessLogReader) {
    this.accessLogWriter = accessLogWriter;
    this.accessLogReader = accessLogReader;
  }

  public void log(RedirectInfo info) {
    accessLogWriter.write(info);
  }

  public long getTotalCount(String alias) {
    return accessLogReader.getTotalCount(alias);
  }
}
