package com.contentdiscovery.recommendation.domain.exception;

import com.contentdiscovery.recommendation.domain.valueobject.UserId;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UserId userId) {
        super("User not found: " + userId.value());
    }
}
