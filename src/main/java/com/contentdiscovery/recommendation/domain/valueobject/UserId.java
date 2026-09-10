package com.contentdiscovery.recommendation.domain.valueobject;

public record UserId(String value) {
    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User id must not be blank");
        }
    }
}
