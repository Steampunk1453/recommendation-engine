package com.contentdiscovery.recommendation.application.usecase;

import com.contentdiscovery.recommendation.domain.exception.UserNotFoundException;
import com.contentdiscovery.recommendation.domain.exception.VideoNotFoundException;
import com.contentdiscovery.recommendation.domain.model.InteractionType;
import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class RegisterUserInteraction {
    private final UserRepository users;
    private final VideoRepository videos;
    private final UserInteractionRepository interactions;

    public RegisterUserInteraction(UserRepository users, VideoRepository videos,
                                   UserInteractionRepository interactions) {
        this.users = users;
        this.videos = videos;
        this.interactions = interactions;
    }

    public UserInteraction execute(UserId userId, VideoId videoId, InteractionType type, Duration watchDuration) {
        if (userId == null || videoId == null || type == null || watchDuration == null
                || watchDuration.isNegative()) {
            throw new IllegalArgumentException("Interaction request is invalid");
        }
        users.load(userId).orElseThrow(() -> new UserNotFoundException(userId));
        videos.load(videoId).orElseThrow(() -> new VideoNotFoundException(videoId));
        return interactions.save(new UserInteraction(UUID.randomUUID(), userId, videoId, type,
                watchDuration, Instant.now()));
    }
}
