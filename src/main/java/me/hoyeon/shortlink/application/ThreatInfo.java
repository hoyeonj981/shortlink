package me.hoyeon.shortlink.application;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThreatInfo {

  private List<Match> matches;

  @Getter
  @Setter
  public static class Match {
    private String threatType;
    private String platformType;
    private String threatEntryType;
    private Map<String, String> threat;
    private ThreatEntryMetadata threatEntryMetadata;
    private String cacheDuration;
  }

  @Getter
  @Setter
  public static class ThreatEntryMetadata {
    private List<Entry> entries;
  }

  @Getter
  @Setter
  public static class Entry {
    private String key;
    private String value;
  }
}