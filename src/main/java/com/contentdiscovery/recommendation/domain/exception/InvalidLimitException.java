package com.contentdiscovery.recommendation.domain.exception;

public class InvalidLimitException extends RuntimeException {
    public InvalidLimitException(int limit) {
        super("Limit must be between 1 and 100: " + limit);
    }
}
