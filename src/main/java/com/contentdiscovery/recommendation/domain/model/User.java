package com.contentdiscovery.recommendation.domain.model;

import com.contentdiscovery.recommendation.domain.valueobject.UserId;

import java.time.Instant;

public record User(UserId id, String username, Instant createdAt) {
    public User {
        if (id == null || username == null || username.isBlank() || createdAt == null) {
            throw new IllegalArgumentException("User metadata is incomplete");
        }
    }
}
