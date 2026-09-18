package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto;

import com.contentdiscovery.recommendation.domain.model.InteractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

public record RegisterInteractionRequest(
        @NotBlank String videoId,
        @NotNull InteractionType type,
        @NotNull Duration watchDuration) {}
