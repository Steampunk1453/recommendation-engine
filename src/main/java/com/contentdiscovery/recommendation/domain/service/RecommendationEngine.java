package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.Recommendation;
import com.contentdiscovery.recommendation.domain.model.User;
import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.domain.model.Video;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RecommendationEngine {
    private final List<CandidateGenerator> generators;
    private final VideoRanker ranker;

    public RecommendationEngine(List<CandidateGenerator> generators, VideoRanker ranker) {
        this.generators = List.copyOf(generators);
        this.ranker = ranker;
    }

    public List<Recommendation> recommend(User user, List<Video> candidates,
                                          List<UserInteraction> interactions, int limit) {
        Map<VideoId, Video> byId = candidates.stream().collect(Collectors.toMap(
                Video::id, video -> video, (first, second) -> first, LinkedHashMap::new));
        PreferenceProfile preferences = PreferenceProfile.calculate(user, interactions, byId);
        Map<VideoId, Video> ordered = new LinkedHashMap<>();
        for (CandidateGenerator generator : generators) {
            generator.generate(candidates, preferences).forEach(video -> ordered.putIfAbsent(video.id(), video));
        }
        return ranker.rank(new ArrayList<>(ordered.values()), preferences, limit);
    }
}
