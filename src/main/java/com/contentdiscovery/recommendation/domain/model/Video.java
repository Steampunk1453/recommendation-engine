package com.contentdiscovery.recommendation.domain.model;

import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public record Video(
        VideoId id,
        String title,
        String creatorId,
        String category,
        Set<String> tags,
        Duration duration,
        Instant createdAt,
        double popularityScore,
        double engagementScore,
        boolean active) {
    public Video {
        if (id == null || title == null || title.isBlank() || creatorId == null || creatorId.isBlank()
                || category == null || category.isBlank() || duration == null || duration.isNegative()
                || duration.isZero() || createdAt == null) {
            throw new IllegalArgumentException("Video metadata is incomplete");
        }
        tags = tags == null ? Set.of() : Set.copyOf(tags);
        if (!Double.isFinite(popularityScore) || !Double.isFinite(engagementScore)
                || popularityScore < 0 || popularityScore > 1 || engagementScore < 0 || engagementScore > 1) {
            throw new IllegalArgumentException("Video scores must be between 0 and 1");
        }
    }
}
