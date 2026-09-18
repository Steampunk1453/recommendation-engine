package com.contentdiscovery.recommendation.domain.valueobject;

public record VideoId(String value) {
    public VideoId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Video id must not be blank");
        }
    }
}
