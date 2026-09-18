package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.mapper;

import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;
import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity.UserInteractionEntity;
import java.time.Duration;

public final class InteractionPersistenceMapper {
    public UserInteraction toDomain(UserInteractionEntity entity) {
        return new UserInteraction(entity.getId(), new UserId(entity.getUserId()), new VideoId(entity.getVideoId()),
                entity.getType(), Duration.ofSeconds(entity.getWatchDurationSeconds()), entity.getCreatedAt());
    }
    public UserInteractionEntity toEntity(UserInteraction interaction) {
        return new UserInteractionEntity(interaction.id(), interaction.userId().value(), interaction.videoId().value(),
                interaction.type(), interaction.watchDuration().toSeconds(), interaction.createdAt());
    }
}
