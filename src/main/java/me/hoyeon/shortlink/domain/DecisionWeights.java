package me.hoyeon.shortlink.domain;

public record DecisionWeights(
    double technicalWeight,
    double contentWeight,
    double behaviorWeight
) {
}
