package com.contentdiscovery.recommendation.domain.service;

import com.contentdiscovery.recommendation.domain.model.Video;

import java.util.List;

@FunctionalInterface
public interface CandidateGenerator {
    List<Video> generate(List<Video> candidates, PreferenceProfile preferences);
}
