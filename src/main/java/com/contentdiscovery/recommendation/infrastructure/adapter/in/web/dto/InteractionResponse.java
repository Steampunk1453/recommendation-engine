package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto;

import com.contentdiscovery.recommendation.domain.model.InteractionType;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;

public record InteractionResponse(UUID id, String userId, String videoId, InteractionType type,
                                  Duration watchDuration, Instant createdAt) {}
