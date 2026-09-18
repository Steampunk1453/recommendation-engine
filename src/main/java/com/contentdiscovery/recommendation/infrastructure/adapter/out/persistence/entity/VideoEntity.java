package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "videos")
public class VideoEntity {
    @Id
    private String id;
    @Column(nullable = false) private String title;
    @Column(name = "creator_id", nullable = false) private String creatorId;
    @Column(nullable = false) private String category;
    private String tags;
    @Column(name = "duration_seconds", nullable = false) private long durationSeconds;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "popularity_score", nullable = false) private double popularityScore;
    @Column(name = "engagement_score", nullable = false) private double engagementScore;
    @Column(nullable = false) private boolean active;

    protected VideoEntity() {}

    public VideoEntity(String id, String title, String creatorId, String category, String tags, long durationSeconds,
                       Instant createdAt, double popularityScore, double engagementScore, boolean active) {
        this.id = id; this.title = title; this.creatorId = creatorId; this.category = category; this.tags = tags;
        this.durationSeconds = durationSeconds; this.createdAt = createdAt; this.popularityScore = popularityScore;
        this.engagementScore = engagementScore; this.active = active;
    }
    public String getId() { return id; } public String getTitle() { return title; } public String getCreatorId() { return creatorId; }
    public String getCategory() { return category; } public String getTags() { return tags; }
    public long getDurationSeconds() { return durationSeconds; } public Instant getCreatedAt() { return createdAt; }
    public double getPopularityScore() { return popularityScore; } public double getEngagementScore() { return engagementScore; }
    public boolean isActive() { return active; }
}
