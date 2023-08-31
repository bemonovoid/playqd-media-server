package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.jpa.entity.BrowsableObjectEntity;
import io.playqd.mediaserver.persistence.jpa.repository.BrowsableObjectRepository;
import io.playqd.mediaserver.service.upnp.server.UUIDV3Ids;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
class JpaBrowsableObjectDao implements BrowsableObjectDao {

    private final BrowsableObjectRepository repository;

    JpaBrowsableObjectDao(BrowsableObjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public long countChildren(long parentId) {
        return repository.countByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasChildren(long id) {
        return repository.findFirstByParentId(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public PersistedBrowsableObject getOne(long id) {
        return fromEntity(repository.get(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersistedBrowsableObject> getOneByObjectId(String objectId) {
        return repository.findByObjectId(objectId).map(JpaBrowsableObjectDao::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistedBrowsableObject> getRoot() {
        return repository.findByParentIsNull().stream().map(JpaBrowsableObjectDao::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersistedBrowsableObject> getChildren(long parentId, PageRequest request) {
        return repository.findAllByParentId(parentId, request).map(JpaBrowsableObjectDao::fromEntity);
    }

    @Override
    public PersistedBrowsableObject save(Consumer<BrowsableObjectSetter> consumer) {
        var entity = new BrowsableObjectEntity();
        consumer.accept(entity);
        entity.setObjectId(calculateObjectId(entity));
        return fromEntity(repository.saveAndFlush(entity));
    }

    @Override
    public PersistedBrowsableObject save(PersistedBrowsableObject parent,
                                         List<Consumer<BrowsableObjectSetter>> childrenObjects) {

        var parentEntity = repository.get(parent.id());

        for (Consumer<BrowsableObjectSetter> consumer : childrenObjects) {
            var entity = new BrowsableObjectEntity();
            consumer.accept(entity);
            entity.setParent(parentEntity);
            entity.setParentId(parentEntity.getId());
            entity.setObjectId(calculateObjectId(entity));
            entity = repository.save(entity);
            var children = parentEntity.getChildren();
            if (children == null) {
                children = new ArrayList<>(childrenObjects.size());
            }
            children.add(entity);
        }
        return fromEntity(parentEntity);
    }

    private static String calculateObjectId(BrowsableObjectEntity entity) {
        if (StringUtils.hasText(entity.getLocation())) {
            return UUIDV3Ids.create(entity.getLocation());
        }
        throw new IllegalArgumentException("Cannot calculate browsable object id: 'location' is null or empty.");
    }

    private static PersistedBrowsableObject fromEntity(BrowsableObjectEntity entity) {
        //noinspection DataFlowIssue
        return new PersistedBrowsableObject(
                entity.getId(),
                entity.getParentId(),
                entity.getObjectId(),
                entity.getDcTitle(),
                Paths.get(entity.getLocation()),
                entity.getUpnpClass(),
                entity::getChildCountAvailable,
                entity.getChildContainerCount());
    }

}
