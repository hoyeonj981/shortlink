package me.hoyeon.shortlink.domain;

public record DecisionThresholds(
    double blockThreshold,
    double warnThreshold
) {

}
