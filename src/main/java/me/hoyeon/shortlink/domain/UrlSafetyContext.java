package me.hoyeon.shortlink.domain;

import java.util.Objects;

public class UrlSafetyContext {

  private final ActorInfo actorInfo;
  private final OriginalUrl originalUrl;
  private final TechnicalRisk technicalRisk;
  private final ContentRisk contentRisk;
  private final BehaviorRisk behaviorRisk;

  public UrlSafetyContext(ActorInfo actorInfo, OriginalUrl originalUrl, TechnicalRisk technicalRisk,
      ContentRisk contentRisk, BehaviorRisk behaviorRisk) {
    this.actorInfo = actorInfo;
    this.originalUrl = originalUrl;
    this.technicalRisk = technicalRisk;
    this.contentRisk = contentRisk;
    this.behaviorRisk = behaviorRisk;
  }

  public ActorInfo getActorInfo() {
    return actorInfo;
  }

  public OriginalUrl getOriginalUrl() {
    return originalUrl;
  }

  public TechnicalRisk getTechnicalRisk() {
    return technicalRisk;
  }

  public ContentRisk getContentRisk() {
    return contentRisk;
  }

  public BehaviorRisk getBehaviorRisk() {
    return behaviorRisk;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UrlSafetyContext that = (UrlSafetyContext) o;
    return Objects.equals(actorInfo, that.actorInfo) && Objects.equals(
        originalUrl, that.originalUrl) && Objects.equals(technicalRisk, that.technicalRisk)
        && Objects.equals(contentRisk, that.contentRisk) && Objects.equals(
        behaviorRisk, that.behaviorRisk);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actorInfo, originalUrl, technicalRisk, contentRisk, behaviorRisk);
  }
}
