package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.*;
import com.contentdiscovery.recommendation.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationEngineTest {
    private final Instant now = Instant.parse("2026-09-03T00:00:00Z");
    private Video video(String id, String creator, String category, String... tags) {
        return new Video(new VideoId(id), id, creator, category, Set.of(tags), Duration.ofMinutes(10), now, .8, .7, true);
    }

    @Test
    void excludesDislikedAndWatchedVideosAndRanksMatchingTags() {
        var user = new User(new UserId("u"), "user", now);
        var liked = video("liked", "a", "technology", "java");
        var match = video("match", "b", "technology", "java");
        var disliked = video("bad", "c", "technology", "java");
        var engine = new RecommendationEngine(List.of(new PersonalizedCandidateGenerator(), new TagCandidateGenerator()),
                new VideoRanker(new RecommendationScorer(java.time.Clock.fixed(now, java.time.ZoneOffset.UTC))));
        var result = engine.recommend(user, List.of(liked, match, disliked),
                List.of(new UserInteraction(UUID.randomUUID(), user.id(), liked.id(), InteractionType.LIKE,
                                Duration.ofMinutes(8), now),
                        new UserInteraction(UUID.randomUUID(), user.id(), disliked.id(), InteractionType.DISLIKE,
                                Duration.ZERO, now)), 10);
        assertEquals(List.of("match"), result.stream().map(r -> r.video().id().value()).toList());
    }

    @Test
    void scorerUsesConfiguredWeights() {
        assertEquals(1.0, RecommendationScorer.CATEGORY_WEIGHT + RecommendationScorer.TAG_WEIGHT
                + RecommendationScorer.WATCH_WEIGHT + RecommendationScorer.POPULARITY_WEIGHT
                + RecommendationScorer.RECENCY_WEIGHT + RecommendationScorer.ENGAGEMENT_WEIGHT);
    }

    @Test
    void watchRatioInfluencesPreferenceAndScore() {
        var user = new User(new UserId("u"), "user", now);
        var historyVideo = video("history", "history-creator", "technology", "java");
        var candidate = video("candidate", "candidate-creator", "technology", "java");
        var shortWatch = new UserInteraction(UUID.randomUUID(), user.id(), historyVideo.id(),
                InteractionType.LIKE, Duration.ofMinutes(1), now);
        var fullWatch = new UserInteraction(UUID.randomUUID(), user.id(), historyVideo.id(),
                InteractionType.LIKE, Duration.ofMinutes(10), now);
        var shortProfile = PreferenceProfile.calculate(user, List.of(shortWatch),
                Map.of(historyVideo.id(), historyVideo));
        var fullProfile = PreferenceProfile.calculate(user, List.of(fullWatch),
                Map.of(historyVideo.id(), historyVideo));
        var scorer = new RecommendationScorer(java.time.Clock.fixed(now, java.time.ZoneOffset.UTC));
        assertTrue(scorer.score(candidate, fullProfile) > scorer.score(candidate, shortProfile));
        assertEquals(.1, shortProfile.watchScore(candidate), 1e-9);
        assertEquals(1.0, fullProfile.watchScore(candidate), 1e-9);
        assertEquals(RecommendationScorer.WATCH_WEIGHT * .9,
                scorer.score(candidate, fullProfile) - scorer.score(candidate, shortProfile), 1e-9);
    }

    @Test
    void customCandidateGeneratorsParticipateWithoutEngineChanges() {
        var user = new User(new UserId("u"), "user", now);
        var candidate = video("candidate", "creator", "technology", "java");
        CandidateGenerator custom = (candidates, preferences) -> List.of(candidate);
        var engine = new RecommendationEngine(List.of(custom),
                new VideoRanker(new RecommendationScorer(java.time.Clock.fixed(now, java.time.ZoneOffset.UTC))));

        var result = engine.recommend(user, List.of(candidate), List.of(), 1);

        assertEquals(List.of(candidate.id()), result.stream().map(r -> r.video().id()).toList());
    }

    @Test
    void diversityCapsCreatorAndCategoryRepetition() {
        var user = new User(new UserId("u"), "user", now);
        var candidates = List.of(
                video("a1", "creator-a", "category-a"),
                video("a2", "creator-a", "category-a"),
                video("b1", "creator-b", "category-b"),
                video("b2", "creator-b", "category-b"),
                video("c1", "creator-c", "category-c"),
                video("c2", "creator-c", "category-c"));
        var engine = new RecommendationEngine(List.of((items, preferences) -> items),
                new VideoRanker(new RecommendationScorer(java.time.Clock.fixed(now, java.time.ZoneOffset.UTC))));

        var result = engine.recommend(user, candidates, List.of(), 6);
        var creatorCounts = new HashMap<String, Integer>();
        var categoryCounts = new HashMap<String, Integer>();
        result.forEach(recommendation -> {
            creatorCounts.merge(recommendation.video().creatorId(), 1, Integer::sum);
            categoryCounts.merge(recommendation.video().category(), 1, Integer::sum);
        });

        assertTrue(creatorCounts.values().stream().allMatch(count -> count <= 2));
        assertTrue(categoryCounts.values().stream().allMatch(count -> count <= 2));
    }
}
