package me.hoyeon.shortlink.application;

import java.util.List;
import me.hoyeon.shortlink.domain.OriginalUrl;

public interface UrlSafetyService {

  ThreatInfo assess(List<OriginalUrl> urls);
}
