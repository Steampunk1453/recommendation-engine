package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.controller;

import com.contentdiscovery.recommendation.application.usecase.RegisterUserInteraction;
import com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto.*;
import com.contentdiscovery.recommendation.infrastructure.adapter.in.web.mapper.RecommendationWebMapper;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/interactions")
@Tag(name = "Interactions")
public class InteractionController {
    private final RegisterUserInteraction useCase;
    private final RecommendationWebMapper mapper = new RecommendationWebMapper();
    public InteractionController(RegisterUserInteraction useCase) { this.useCase = useCase; }

    @PostMapping
    @Operation(summary = "Register a user interaction with a video")
    public InteractionResponse register(@PathVariable @NotBlank String userId,
                                        @Valid @RequestBody RegisterInteractionRequest request) {
        return mapper.toResponse(useCase.execute(new UserId(userId), new VideoId(request.videoId()),
                request.type(), request.watchDuration()));
    }
}
