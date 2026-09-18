package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.Recommendation;
import com.contentdiscovery.recommendation.domain.model.Video;
import com.contentdiscovery.recommendation.domain.valueobject.Score;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class VideoRanker {
    private final RecommendationScorer scorer;

    public VideoRanker(RecommendationScorer scorer) {
        this.scorer = scorer;
    }

    public List<Recommendation> rank(List<Video> candidates, PreferenceProfile preferences, int limit) {
        if (limit < 1) throw new IllegalArgumentException("Recommendation limit must be positive");
        List<Recommendation> scored = candidates.stream()
                .filter(Video::active)
                .filter(video -> !preferences.dislikedVideos().contains(video.id()))
                .filter(video -> !preferences.watchedVideos().contains(video.id()))
                .map(video -> new Recommendation(video, new Score(scorer.score(video, preferences)),
                        preferences.categories().contains(video.category()) ? "Matches your interests" : "Popular and timely"))
                .sorted(Comparator.comparingDouble((Recommendation r) -> r.score().value()).reversed()
                        .thenComparing(r -> r.video().id().value()))
                .toList();
        List<Recommendation> diverse = new ArrayList<>();
        Map<String, Integer> creators = new HashMap<>();
        Map<String, Integer> categories = new HashMap<>();
        Set<VideoId> selected = new HashSet<>();
        int maxPerCreator = 2;
        int maxPerCategory = 2;
        for (Recommendation recommendation : scored) {
            if (diverse.size() >= limit) break;
            String creator = recommendation.video().creatorId();
            String category = recommendation.video().category();
            if (creators.getOrDefault(creator, 0) < maxPerCreator
                    && categories.getOrDefault(category, 0) < maxPerCategory) {
                diverse.add(recommendation);
                selected.add(recommendation.video().id());
                creators.merge(creator, 1, Integer::sum);
                categories.merge(category, 1, Integer::sum);
            }
        }
        if (diverse.size() < limit) {
            for (Recommendation recommendation : scored) {
                if (diverse.size() >= limit) break;
                if (selected.add(recommendation.video().id())) diverse.add(recommendation);
            }
        }
        return List.copyOf(diverse);
    }
}
