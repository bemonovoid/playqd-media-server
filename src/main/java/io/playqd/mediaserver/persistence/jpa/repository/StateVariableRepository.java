package io.playqd.mediaserver.persistence.jpa.repository;

import io.playqd.mediaserver.persistence.jpa.entity.StateVariableJpaEntity;
import io.playqd.mediaserver.service.upnp.service.StateVariableName;

import java.util.Optional;

public interface StateVariableRepository extends IdentityJpaRepository<StateVariableJpaEntity> {

    Optional<StateVariableJpaEntity> findFirstByKey(StateVariableName stateVariable);

}
