package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto;

public record RecommendationDto(String videoId, String title, String creatorId, String category,
                                double score, String reason) {}
