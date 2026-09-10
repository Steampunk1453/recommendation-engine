package com.contentdiscovery.recommendation.domain.port;

import com.contentdiscovery.recommendation.domain.model.User;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> load(UserId userId);
}
