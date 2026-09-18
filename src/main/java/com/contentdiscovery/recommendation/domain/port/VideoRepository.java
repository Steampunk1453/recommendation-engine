package com.contentdiscovery.recommendation.domain.port;

import com.contentdiscovery.recommendation.domain.model.Video;
import com.contentdiscovery.recommendation.domain.valueobject.VideoId;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface VideoRepository {
    Optional<Video> load(VideoId videoId);

    List<Video> loadAll(Set<VideoId> videoIds);

    List<Video> loadCandidates(CandidateQuery query);

    record CandidateQuery(int page, int size, Set<VideoId> excludedVideoIds) {
        public CandidateQuery {
            if (page < 0 || size < 1) {
                throw new IllegalArgumentException("Invalid candidate page");
            }
            excludedVideoIds = excludedVideoIds == null ? Set.of() : Set.copyOf(excludedVideoIds);
        }
    }
}
