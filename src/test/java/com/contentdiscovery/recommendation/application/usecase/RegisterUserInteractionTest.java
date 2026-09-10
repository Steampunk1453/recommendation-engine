package com.contentdiscovery.recommendation.application.usecase;

import com.contentdiscovery.recommendation.domain.model.*;
import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.contentdiscovery.recommendation.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserInteractionTest {
    @Test
    void validatesReferencesBeforeSavingInteraction() {
        var users = mock(UserRepository.class);
        var videos = mock(VideoRepository.class);
        var interactions = mock(UserInteractionRepository.class);
        var userId = new UserId("u");
        var videoId = new VideoId("v");
        when(users.load(userId)).thenReturn(Optional.of(new User(userId, "user", Instant.EPOCH)));
        when(videos.load(videoId)).thenReturn(Optional.of(new Video(videoId, "Video", "Creator", "category",
                java.util.Set.of("tag"), Duration.ofMinutes(1), Instant.EPOCH, .5, .5, true)));
        when(interactions.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = new RegisterUserInteraction(users, videos, interactions)
                .execute(userId, videoId, InteractionType.LIKE, Duration.ZERO);

        assertEquals(userId, result.userId());
        assertEquals(videoId, result.videoId());
        assertNotNull(result.id());
        assertNotNull(result.createdAt());
        verify(interactions).save(any(UserInteraction.class));
    }

    @Test
    void rejectsMissingOrNegativeWatchDurationBeforePersistence() {
        var users = mock(UserRepository.class);
        var videos = mock(VideoRepository.class);
        var interactions = mock(UserInteractionRepository.class);
        var useCase = new RegisterUserInteraction(users, videos, interactions);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(new UserId("u"), new VideoId("v"), InteractionType.VIEW, null));
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(new UserId("u"), new VideoId("v"), InteractionType.VIEW,
                        Duration.ofSeconds(-1)));
        verifyNoInteractions(users, videos, interactions);
    }
}
