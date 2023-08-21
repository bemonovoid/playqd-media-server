package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import io.playqd.mediaserver.model.StateVariables;

import java.io.Serializable;
import java.util.Optional;

public interface StateVariableContextHolder {

    <T> T getOrThrow(StateVariables stateVariable);

    <T> Optional<T> get(StateVariables stateVariable);

    <T extends Serializable> T set(StateVariables stateVariable, T newValue);

}
