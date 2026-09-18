package com.contentdiscovery.recommendation.domain.model;

import com.contentdiscovery.recommendation.domain.valueobject.Score;

public record Recommendation(Video video, Score score, String reason) {
    public Recommendation {
        if (video == null || score == null || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Recommendation is incomplete");
        }
    }
}
