package me.hoyeon.shortlink.application;

import java.util.concurrent.atomic.AtomicLong;

public class SimpleIdGenerator {

  private final AtomicLong value;

  public SimpleIdGenerator() {
    this.value = new AtomicLong(1L);
  }

  public Long getId() {
    return this.value.getAndAdd(1);
  }
}
