package io.playqd.mediaserver.persistence.jpa.repository;

import io.playqd.mediaserver.persistence.jpa.entity.BrowsableObjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BrowsableObjectRepository extends IdentityJpaRepository<BrowsableObjectEntity> {

    default BrowsableObjectEntity getByObjectId(String objectId) {
        return findByObjectId(objectId).orElseThrow(() -> new RuntimeException(
                String.format("Browsable object with objectId '%s' was not found.", objectId)));
    }

    Optional<BrowsableObjectEntity> findByObjectId(String objectId);

    Optional<BrowsableObjectEntity> findFirstByParentId(long parentId);

    long countByParentId(long parentId);

    List<BrowsableObjectEntity> findByParentIsNull();

    Page<BrowsableObjectEntity> findAllByParentId(long parentId, Pageable pageable);

}
