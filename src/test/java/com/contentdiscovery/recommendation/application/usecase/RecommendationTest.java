package com.contentdiscovery.recommendation.application.usecase;

import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.contentdiscovery.recommendation.domain.model.User;
import com.contentdiscovery.recommendation.domain.service.RecommendationEngine;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RecommendationTest {
    @Test
    void retrievesPagedCandidatesAndDelegatesToEngine() {
        var users = mock(UserRepository.class);
        var videos = mock(VideoRepository.class);
        var interactions = mock(UserInteractionRepository.class);
        var engine = mock(RecommendationEngine.class);
        var userId = new UserId("u");
        var user = new User(userId, "user", Instant.EPOCH);
        when(users.load(userId)).thenReturn(Optional.of(user));
        when(interactions.loadRecent(userId, 500)).thenReturn(List.of());
        when(videos.loadCandidates(any())).thenReturn(List.of());
        when(videos.loadAll(any())).thenReturn(List.of());
        when(engine.recommend(eq(user), anyList(), anyList(), eq(20))).thenReturn(List.of());

        assertTrue(new GetRecommendations(users, videos, interactions, engine)
                .execute(userId, 20).isEmpty());

        verify(videos).loadCandidates(any());
        verify(engine).recommend(eq(user), anyList(), anyList(), eq(20));
    }
}
