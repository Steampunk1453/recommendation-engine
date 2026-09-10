package com.contentdiscovery.recommendation.application.usecase;

import com.contentdiscovery.recommendation.domain.exception.InvalidLimitException;
import com.contentdiscovery.recommendation.domain.exception.UserNotFoundException;
import com.contentdiscovery.recommendation.domain.model.Recommendation;
import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.domain.model.Video;
import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.contentdiscovery.recommendation.domain.service.RecommendationEngine;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class GetRecommendations {
    private final UserRepository users;
    private final VideoRepository videos;
    private final UserInteractionRepository interactions;
    private final RecommendationEngine engine;

    public GetRecommendations(UserRepository users, VideoRepository videos,
                               UserInteractionRepository interactions, RecommendationEngine engine) {
        this.users = users;
        this.videos = videos;
        this.interactions = interactions;
        this.engine = engine;
    }

    public List<Recommendation> execute(UserId userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (limit < 1 || limit > 100) {
            throw new InvalidLimitException(limit);
        }

        var user = users.load(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<UserInteraction> history = interactions.loadRecent(userId, 500);
        var excluded = new HashSet<>(history.stream().map(UserInteraction::videoId).toList());
        List<Video> pool = videos.loadCandidates(
                new VideoRepository.CandidateQuery(0, Math.min(500, Math.max(50, limit * 8)), excluded));
        var historyVideos = videos.loadAll(history.stream()
                .map(UserInteraction::videoId)
                .collect(Collectors.toSet()));
        var enriched = new ArrayList<>(pool);
        historyVideos.forEach(video -> {
            if (enriched.stream().noneMatch(candidate -> candidate.id().equals(video.id()))) {
                enriched.add(video);
            }
        });
        return engine.recommend(user, enriched, history, limit);
    }
}
