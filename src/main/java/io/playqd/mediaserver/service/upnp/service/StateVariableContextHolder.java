package io.playqd.mediaserver.service.upnp.service;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface StateVariableContextHolder {

    <T> T getOrThrow(StateVariableName stateVariable);

    <T> Optional<T> get(StateVariableName stateVariable);

    <T extends Serializable> T getOrUpdate(StateVariableName stateVariable, Supplier<T> newValue);

    <T extends Serializable> void set(StateVariableName stateVariable, T newValue);

    Set<StateVariable> getAll();

}
