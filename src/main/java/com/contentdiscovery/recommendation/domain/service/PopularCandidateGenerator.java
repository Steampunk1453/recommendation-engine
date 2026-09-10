package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.Video;

import java.util.Comparator;
import java.util.List;

public final class PopularCandidateGenerator implements CandidateGenerator {
    @Override
    public List<Video> generate(List<Video> candidates, PreferenceProfile preferences) {
        return candidates.stream().sorted(Comparator.comparingDouble(Video::popularityScore).reversed()).toList();
    }
}
