package io.playqd.mediaserver.persistence.jpa.repository;

import io.playqd.mediaserver.service.upnp.server.service.StateVariableName;
import io.playqd.mediaserver.persistence.jpa.entity.StateVariableJpaEntity;

import java.util.Optional;

public interface StateVariableRepository extends IdentityJpaRepository<StateVariableJpaEntity> {

    Optional<StateVariableJpaEntity> findFirstByKey(StateVariableName stateVariable);

}
