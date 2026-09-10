package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.Video;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public final class RecommendationScorer {
    public static final double CATEGORY_WEIGHT = 0.25;
    public static final double TAG_WEIGHT = 0.20;
    public static final double WATCH_WEIGHT = 0.20;
    public static final double POPULARITY_WEIGHT = 0.10;
    public static final double RECENCY_WEIGHT = 0.10;
    public static final double ENGAGEMENT_WEIGHT = 0.15;

    private final Clock clock;

    public RecommendationScorer() {
        this(Clock.systemUTC());
    }

    public RecommendationScorer(Clock clock) {
        this.clock = clock;
    }

    public double score(Video video, PreferenceProfile preferences) {
        double category = preferences.categoryAffinity().containsKey(video.category()) ? 1.0 : 0.0;
        double tag = video.tags().stream().filter(preferences.tags()::contains).distinct().count()
                / (double) Math.max(1, video.tags().size());
        double watchScore = preferences.watchScore(video);
        double ageDays = Math.max(0, Duration.between(video.createdAt(), Instant.now(clock)).toHours() / 24.0);
        double recency = Math.exp(-ageDays / 30.0);
        return CATEGORY_WEIGHT * category + TAG_WEIGHT * tag + WATCH_WEIGHT * watchScore
                + POPULARITY_WEIGHT * video.popularityScore() + RECENCY_WEIGHT * recency
                + ENGAGEMENT_WEIGHT * video.engagementScore();
    }
}
