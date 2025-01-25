package me.hoyeon.shortlink.domain;

@FunctionalInterface
public interface AliasGenerator {

  String shorten(String base);
}
