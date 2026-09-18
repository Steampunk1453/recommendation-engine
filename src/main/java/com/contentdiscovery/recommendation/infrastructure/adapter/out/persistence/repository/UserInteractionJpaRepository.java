package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.repository;

import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity.UserInteractionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserInteractionJpaRepository extends JpaRepository<UserInteractionEntity, UUID> {
    List<UserInteractionEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
