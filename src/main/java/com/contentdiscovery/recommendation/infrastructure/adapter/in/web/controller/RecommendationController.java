package com.contentdiscovery.recommendation.infrastructure.adapter.in.web.controller;

import com.contentdiscovery.recommendation.application.usecase.GetRecommendations;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import com.contentdiscovery.recommendation.infrastructure.adapter.in.web.dto.RecommendationsResponse;
import com.contentdiscovery.recommendation.infrastructure.adapter.in.web.mapper.RecommendationWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/recommendations")
@Validated
@Tag(name = "Recommendations")
public class RecommendationController {
    private final GetRecommendations useCase;
    private final RecommendationWebMapper mapper = new RecommendationWebMapper();
    public RecommendationController(GetRecommendations useCase) { this.useCase = useCase; }

    @GetMapping
    @Operation(summary = "Get personalized video recommendations")
    public RecommendationsResponse get(@PathVariable @NotBlank String userId,
                                       @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        return mapper.toResponse(userId, useCase.execute(new UserId(userId), limit));
    }
}
