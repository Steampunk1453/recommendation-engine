package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.repository;

import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {}
