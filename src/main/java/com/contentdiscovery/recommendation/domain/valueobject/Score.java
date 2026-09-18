package com.contentdiscovery.recommendation.domain.valueobject;

public record Score(double value) {
    public Score {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0 || value > 1) {
            throw new IllegalArgumentException("Score must be between 0 and 1");
        }
    }
}
