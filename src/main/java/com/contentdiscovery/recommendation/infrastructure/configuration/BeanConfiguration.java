package com.contentdiscovery.recommendation.infrastructure.configuration;

import com.contentdiscovery.recommendation.application.usecase.GetRecommendations;
import com.contentdiscovery.recommendation.application.usecase.RegisterUserInteraction;
import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.contentdiscovery.recommendation.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class BeanConfiguration {
    @Bean
    RecommendationEngine recommendationEngine() {
        return new RecommendationEngine(List.of(new PersonalizedCandidateGenerator(), new TagCandidateGenerator(),
                new PopularCandidateGenerator(), new RecentCandidateGenerator()),
                new VideoRanker(new RecommendationScorer()));
    }
    @Bean
    GetRecommendations getRecommendations(UserRepository users, VideoRepository videos,
            UserInteractionRepository interactions, RecommendationEngine engine) {
        return new GetRecommendations(users, videos, interactions, engine);
    }
    @Bean
    RegisterUserInteraction registerUserInteraction(UserRepository users, VideoRepository videos,
            UserInteractionRepository interactions) {
        return new RegisterUserInteraction(users, videos, interactions);
    }
}
