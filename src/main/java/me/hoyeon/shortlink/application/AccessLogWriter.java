package me.hoyeon.shortlink.application;

public interface AccessLogWriter {

  void write(RedirectInfo info);
}
