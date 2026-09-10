package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.InteractionType;
import com.contentdiscovery.recommendation.domain.model.User;
import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.domain.model.Video;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record PreferenceProfile(
        Set<String> categories,
        Set<String> tags,
        Set<VideoId> dislikedVideos,
        Set<VideoId> watchedVideos,
        Map<String, Double> categoryAffinity,
        Map<String, Double> tagAffinity,
        Map<String, Double> categoryWatchAffinity,
        Map<String, Double> tagWatchAffinity) {
    public PreferenceProfile {
        categories = Set.copyOf(categories);
        tags = Set.copyOf(tags);
        dislikedVideos = Set.copyOf(dislikedVideos);
        watchedVideos = Set.copyOf(watchedVideos);
        categoryAffinity = Map.copyOf(categoryAffinity);
        tagAffinity = Map.copyOf(tagAffinity);
        categoryWatchAffinity = Map.copyOf(categoryWatchAffinity);
        tagWatchAffinity = Map.copyOf(tagWatchAffinity);
    }

    public static PreferenceProfile calculate(User user, List<UserInteraction> interactions,
                                              Map<VideoId, Video> knownVideos) {
        Set<String> categories = new HashSet<>();
        Set<String> tags = new HashSet<>();
        Set<VideoId> disliked = new HashSet<>();
        Set<VideoId> watched = new HashSet<>();
        Map<String, Double> categoryScores = new HashMap<>();
        Map<String, Double> tagScores = new HashMap<>();
        Map<String, Double> categoryWatchTotals = new HashMap<>();
        Map<String, Double> categoryWatchWeights = new HashMap<>();
        Map<String, Double> tagWatchTotals = new HashMap<>();
        Map<String, Double> tagWatchWeights = new HashMap<>();
        for (UserInteraction interaction : interactions) {
            if (interaction.type() == InteractionType.DISLIKE) disliked.add(interaction.videoId());
            if (interaction.type() == InteractionType.VIEW || interaction.type() == InteractionType.SKIP
                    || interaction.type() == InteractionType.LIKE || interaction.type() == InteractionType.SHARE
                    || interaction.type() == InteractionType.COMMENT) {
                watched.add(interaction.videoId());
            }
            Video video = knownVideos.get(interaction.videoId());
            if (video == null) continue;
            double watchRatio = interaction.watchRatio(video.duration());
            if (interaction.type() != InteractionType.DISLIKE) {
                categoryWatchTotals.merge(video.category(), watchRatio, Double::sum);
                categoryWatchWeights.merge(video.category(), 1.0, Double::sum);
                video.tags().forEach(tag -> {
                    tagWatchTotals.merge(tag, watchRatio, Double::sum);
                    tagWatchWeights.merge(tag, 1.0, Double::sum);
                });
            }
            double weight = switch (interaction.type()) {
                case LIKE -> 3.0;
                case SHARE, COMMENT -> 2.0;
                case VIEW -> 1.0;
                case SKIP -> -0.5;
                case DISLIKE -> -2.0;
            };
            if (interaction.type() == InteractionType.VIEW || interaction.type() == InteractionType.SKIP) {
                weight *= watchRatio;
            } else {
                weight *= 0.5 + watchRatio * 0.5;
            }
            double affinityWeight = weight;
            categoryScores.merge(video.category(), affinityWeight, Double::sum);
            video.tags().forEach(tag -> tagScores.merge(tag, affinityWeight, Double::sum));
        }
        categoryScores.values().removeIf(value -> value <= 0);
        tagScores.values().removeIf(value -> value <= 0);
        categories.addAll(categoryScores.keySet());
        tags.addAll(tagScores.keySet());
        Map<String, Double> categoryWatchAffinity = averages(categoryWatchTotals, categoryWatchWeights);
        Map<String, Double> tagWatchAffinity = averages(tagWatchTotals, tagWatchWeights);
        return new PreferenceProfile(categories, tags, disliked, watched, categoryScores, tagScores,
                categoryWatchAffinity, tagWatchAffinity);
    }

    private static Map<String, Double> averages(Map<String, Double> totals, Map<String, Double> weights) {
        Map<String, Double> averages = new HashMap<>();
        totals.forEach((key, total) -> averages.put(key, total / weights.get(key)));
        return averages;
    }

    public double watchScore(Video video) {
        double categoryScore = categoryWatchAffinity.getOrDefault(video.category(), 0.0);
        double tagScore = video.tags().stream()
                .mapToDouble(tag -> tagWatchAffinity.getOrDefault(tag, 0.0))
                .max()
                .orElse(0.0);
        return Math.max(0.0, Math.min(1.0, Math.max(categoryScore, tagScore)));
    }

    public boolean hasPositiveHistory(List<UserInteraction> interactions) {
        return interactions.stream().anyMatch(i -> i.type() == InteractionType.LIKE
                || i.type() == InteractionType.SHARE || i.type() == InteractionType.COMMENT);
    }
}
