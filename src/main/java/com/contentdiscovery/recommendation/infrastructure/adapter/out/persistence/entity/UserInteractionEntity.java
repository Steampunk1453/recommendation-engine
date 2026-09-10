package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity;

import com.contentdiscovery.recommendation.domain.model.InteractionType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_interactions")
public class UserInteractionEntity {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false) private String userId;
    @Column(name = "video_id", nullable = false) private String videoId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private InteractionType type;
    @Column(name = "watch_duration_seconds", nullable = false) private long watchDurationSeconds;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected UserInteractionEntity() {}
    public UserInteractionEntity(UUID id, String userId, String videoId, InteractionType type,
                                 long watchDurationSeconds, Instant createdAt) {
        this.id = id; this.userId = userId; this.videoId = videoId; this.type = type;
        this.watchDurationSeconds = watchDurationSeconds; this.createdAt = createdAt;
    }
    public UUID getId() { return id; } public String getUserId() { return userId; } public String getVideoId() { return videoId; }
    public InteractionType getType() { return type; } public long getWatchDurationSeconds() { return watchDurationSeconds; }
    public Instant getCreatedAt() { return createdAt; }
}
