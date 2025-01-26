package me.hoyeon.shortlink.domain;

@FunctionalInterface
public interface AliasGenerator {

  Alias shorten(String base);
}
