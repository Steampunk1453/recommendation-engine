package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence;

import com.contentdiscovery.recommendation.domain.port.UserInteractionRepository;
import com.contentdiscovery.recommendation.domain.port.UserRepository;
import com.contentdiscovery.recommendation.domain.port.VideoRepository;
import com.contentdiscovery.recommendation.domain.model.*;
import com.contentdiscovery.recommendation.domain.valueobject.*;
import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.mapper.*;
import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
class UserPersistenceAdapter implements UserRepository {
    private final UserJpaRepository repository; private final UserPersistenceMapper mapper = new UserPersistenceMapper();
    UserPersistenceAdapter(UserJpaRepository repository) { this.repository = repository; }
    public Optional<User> load(UserId id) { return repository.findById(id.value()).map(mapper::toDomain); }
}

@Component
class VideoPersistenceAdapter implements VideoRepository {
    private final VideoJpaRepository repository; private final VideoPersistenceMapper mapper = new VideoPersistenceMapper();
    VideoPersistenceAdapter(VideoJpaRepository repository) { this.repository = repository; }
    public Optional<Video> load(VideoId id) { return repository.findById(id.value()).map(mapper::toDomain); }
    public List<Video> loadAll(Set<VideoId> ids) { return repository.findAllById(ids.stream().map(VideoId::value).toList()).stream().map(mapper::toDomain).toList(); }
    public List<Video> loadCandidates(VideoRepository.CandidateQuery query) {
        var pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "popularityScore").and(Sort.by(Sort.Direction.DESC, "createdAt")));
        if (query.excludedVideoIds().isEmpty()) return repository.findByActiveTrue(pageable).map(mapper::toDomain).getContent();
        return repository.findByActiveTrueAndIdNotIn(query.excludedVideoIds().stream().map(VideoId::value).toList(), pageable).map(mapper::toDomain).getContent();
    }
}

@Component
class InteractionPersistenceAdapter implements UserInteractionRepository {
    private final UserInteractionJpaRepository repository; private final InteractionPersistenceMapper mapper = new InteractionPersistenceMapper();
    InteractionPersistenceAdapter(UserInteractionJpaRepository repository) { this.repository = repository; }
    public List<UserInteraction> loadRecent(UserId id, int limit) { return repository.findByUserIdOrderByCreatedAtDesc(id.value(), PageRequest.of(0, limit)).stream().map(mapper::toDomain).toList(); }
    public UserInteraction save(UserInteraction interaction) { return mapper.toDomain(repository.save(mapper.toEntity(interaction))); }
}
