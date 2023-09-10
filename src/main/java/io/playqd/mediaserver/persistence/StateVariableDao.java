package io.playqd.mediaserver.persistence;

import io.playqd.mediaserver.service.upnp.service.StateVariable;
import io.playqd.mediaserver.service.upnp.service.StateVariableName;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public interface StateVariableDao {

    Set<StateVariable> getAll();

    <T> Optional<T> get(StateVariableName stateVariable);

    <T extends Serializable> void set(StateVariableName stateVariable, T value);

}
