package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.repository;

import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, String> {
    Page<VideoEntity> findByActiveTrue(Pageable pageable);
    Page<VideoEntity> findByActiveTrueAndIdNotIn(Collection<String> ids, Pageable pageable);
}
