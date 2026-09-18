package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.mapper;

import com.contentdiscovery.recommendation.domain.model.Recommendation;
import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto.*;
import java.util.List;

public final class RecommendationWebMapper {
    public RecommendationsResponse toResponse(String userId, List<Recommendation> recommendations) {
        return new RecommendationsResponse(userId, recommendations.stream().map(this::toDto).toList());
    }
    public RecommendationDto toDto(Recommendation recommendation) {
        var video = recommendation.video();
        return new RecommendationDto(video.id().value(), video.title(), video.creatorId(), video.category(),
                recommendation.score().value(), recommendation.reason());
    }
    public InteractionResponse toResponse(UserInteraction interaction) {
        return new InteractionResponse(interaction.id(), interaction.userId().value(), interaction.videoId().value(),
                interaction.type(), interaction.watchDuration(), interaction.createdAt());
    }
}
