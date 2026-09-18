package com.contentdiscovery.recommendation.domain.exception;

import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(VideoId videoId) {
        super("Video not found: " + videoId.value());
    }
}
