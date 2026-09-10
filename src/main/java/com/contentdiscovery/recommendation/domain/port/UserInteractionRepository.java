package com.contentdiscovery.recommendation.domain.port;

import com.contentdiscovery.recommendation.domain.model.UserInteraction;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;

import java.util.List;

public interface UserInteractionRepository {
    List<UserInteraction> loadRecent(UserId userId, int limit);

    UserInteraction save(UserInteraction interaction);
}
