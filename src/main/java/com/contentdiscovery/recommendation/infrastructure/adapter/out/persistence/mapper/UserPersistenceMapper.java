package com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.mapper;

import com.contentdiscovery.recommendation.domain.model.User;
import com.contentdiscovery.recommendation.domain.valueobject.UserId;
import com.contentdiscovery.recommendation.infrastructure.adapter.out.persistence.entity.UserEntity;

public final class UserPersistenceMapper {
    public User toDomain(UserEntity entity) {
        return new User(new UserId(entity.getId()), entity.getUsername(), entity.getCreatedAt());
    }
}
