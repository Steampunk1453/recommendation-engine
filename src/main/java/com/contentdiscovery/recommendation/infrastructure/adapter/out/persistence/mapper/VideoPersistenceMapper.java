package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.mapper;

import com.contentdiscovery.recommendation.domain.model.Video;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;
import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity.VideoEntity;
import java.util.Arrays;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

public final class VideoPersistenceMapper {
    public Video toDomain(VideoEntity entity) {
        Set<String> tags = entity.getTags() == null || entity.getTags().isBlank() ? Set.of()
                : Arrays.stream(entity.getTags().split(",")).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());
        return new Video(new VideoId(entity.getId()), entity.getTitle(), entity.getCreatorId(), entity.getCategory(), tags,
                Duration.ofSeconds(entity.getDurationSeconds()), entity.getCreatedAt(), entity.getPopularityScore(),
                entity.getEngagementScore(), entity.isActive());
    }
}
