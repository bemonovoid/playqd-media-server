package io.playqd.mediaserver.persistence;

import io.playqd.mediaserver.model.StateVariables;

import java.io.Serializable;
import java.util.Optional;

public interface StateVariableDao {

    <T> Optional<T> get(StateVariables stateVariable);

    <T extends Serializable> void set(StateVariables stateVariable, T value);

}
