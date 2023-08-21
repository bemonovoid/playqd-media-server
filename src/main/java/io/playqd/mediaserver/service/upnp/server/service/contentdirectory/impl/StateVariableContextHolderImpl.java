package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.model.StateVariables;
import io.playqd.mediaserver.model.event.MediaSourceContentChangedEvent;
import io.playqd.mediaserver.persistence.StateVariableDao;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.StateVariableContextHolder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
class StateVariableContextHolderImpl implements StateVariableContextHolder {

    private final StateVariableDao stateVariableDao;

    StateVariableContextHolderImpl(StateVariableDao stateVariableDao) {
        this.stateVariableDao = stateVariableDao;
    }

    @Override
    public <T> T getOrThrow(StateVariables stateVariable) {
        Optional<T> mayBeSystemUpdateId = get(stateVariable);
        if (mayBeSystemUpdateId.isEmpty()) {
            throw new PlayqdException(stateVariable.getVariableName() + " was not found");
        }
        return mayBeSystemUpdateId.get();
    }

    @Override
    public <T> Optional<T> get(StateVariables stateVariable) {
        return stateVariableDao.get(stateVariable);
    }

    @Override
    public <T extends Serializable> T set(StateVariables stateVariable, T newValue) {
        return stateVariableDao.set(stateVariable, newValue);
    }

    @EventListener(MediaSourceContentChangedEvent.class)
    public void handleMediaSourceContentChangedEvent(MediaSourceContentChangedEvent event) {
        Integer systemUpdateId = getOrThrow(StateVariables.SYSTEM_UPDATE_ID);
        set(StateVariables.SYSTEM_UPDATE_ID, systemUpdateId + 1);
    }

}
