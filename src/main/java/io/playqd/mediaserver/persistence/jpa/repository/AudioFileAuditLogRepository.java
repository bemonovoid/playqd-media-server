package io.playqd.mediaserver.persistence.jpa.repository;

import io.playqd.mediaserver.persistence.jpa.entity.AudioFileSourceAuditLogJpaEntity;

import java.util.Optional;

public interface AudioFileAuditLogRepository extends IdentityJpaRepository<AudioFileSourceAuditLogJpaEntity> {

    default Optional<AudioFileSourceAuditLogJpaEntity> getOne() {
        return findById(1L);
    }
}
