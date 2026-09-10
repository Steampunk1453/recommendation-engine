package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.Video;

import java.util.Comparator;
import java.util.List;

public final class PersonalizedCandidateGenerator implements CandidateGenerator {
    @Override
    public List<Video> generate(List<Video> candidates, PreferenceProfile preferences) {
        return candidates.stream().sorted(Comparator.comparingDouble((Video video) ->
                preferences.categoryAffinity().getOrDefault(video.category(), 0.0)
                        + video.tags().stream().mapToDouble(tag -> preferences.tagAffinity().getOrDefault(tag, 0.0)).sum()
        ).reversed()).toList();
    }
}
