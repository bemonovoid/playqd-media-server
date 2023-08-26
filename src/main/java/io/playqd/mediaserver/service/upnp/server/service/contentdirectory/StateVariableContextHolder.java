package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import io.playqd.mediaserver.model.StateVariables;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

public interface StateVariableContextHolder {

    <T> T getOrThrow(StateVariables stateVariable);

    <T> Optional<T> get(StateVariables stateVariable);

    <T extends Serializable> T getOrUpdate(StateVariables stateVariable, Supplier<T> newValue);

    <T extends Serializable> void set(StateVariables stateVariable, T newValue);

}
