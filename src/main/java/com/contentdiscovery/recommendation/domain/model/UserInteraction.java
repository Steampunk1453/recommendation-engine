package com.contentdiscovery.recommendation.domain.model;

import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record UserInteraction(UUID id, UserId userId, VideoId videoId, InteractionType type,
                              Duration watchDuration, Instant createdAt) {
    public UserInteraction {
        if (id == null || userId == null || videoId == null || type == null || watchDuration == null
                || watchDuration.isNegative() || createdAt == null) {
            throw new IllegalArgumentException("Interaction is incomplete");
        }
    }

    public double watchRatio(Duration videoDuration) {
        if (videoDuration == null || videoDuration.isZero() || videoDuration.isNegative()) return 0;
        return Math.min(1.0, watchDuration.toNanos() / (double) videoDuration.toNanos());
    }
}
