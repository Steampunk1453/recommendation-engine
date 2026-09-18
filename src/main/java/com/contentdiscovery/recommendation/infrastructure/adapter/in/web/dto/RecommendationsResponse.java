package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto;

import java.util.List;

public record RecommendationsResponse(String userId, List<RecommendationDto> recommendations) {}
