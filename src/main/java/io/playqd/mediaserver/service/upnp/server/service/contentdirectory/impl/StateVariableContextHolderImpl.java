package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.model.StateVariables;
import io.playqd.mediaserver.model.event.AudioFileByteStreamRequestedEvent;
import io.playqd.mediaserver.model.event.MediaSourceContentChangedEvent;
import io.playqd.mediaserver.persistence.StateVariableDao;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.StateVariableContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Component
class StateVariableContextHolderImpl implements StateVariableContextHolder {

    private final StateVariableDao stateVariableDao;

    StateVariableContextHolderImpl(StateVariableDao stateVariableDao) {
        this.stateVariableDao = stateVariableDao;
    }

    @Override
    public <T> T getOrThrow(StateVariables stateVariable) {
        Optional<T> mayBeStateVariable = get(stateVariable);
        if (mayBeStateVariable.isEmpty()) {
            throw new PlayqdException(stateVariable.getVariableName() + " was not found");
        }
        return mayBeStateVariable.get();
    }

    @Override
    public <T> Optional<T> get(StateVariables stateVariable) {
        return stateVariableDao.get(stateVariable);
    }

    @Override
    public <T extends Serializable> T getOrUpdate(StateVariables stateVariable, Supplier<T> newValue) {
        Optional<T> mayBeStateVariable = get(stateVariable);
        if (mayBeStateVariable.isEmpty()) {
            set(stateVariable, newValue.get());
        }
        return mayBeStateVariable.orElse(newValue.get());
    }

    @Override
    public <T extends Serializable> void set(StateVariables stateVariable, T newValue) {
        stateVariableDao.set(stateVariable, newValue);
    }

    @EventListener(MediaSourceContentChangedEvent.class)
    public void handleMediaSourceContentChangedEvent(MediaSourceContentChangedEvent event) {
        updateSystemUpdateId();
    }

    @EventListener(AudioFileByteStreamRequestedEvent.class)
    public void handleAudioFileByteStreamRequestedEvent(AudioFileByteStreamRequestedEvent event) {
        updateSystemUpdateId();
    }

    private void updateSystemUpdateId() {
        Integer oldSystemUpdateId = getOrThrow(StateVariables.SYSTEM_UPDATE_ID);
        var newSystemUpdateId = oldSystemUpdateId + 1;
        set(StateVariables.SYSTEM_UPDATE_ID, newSystemUpdateId);
        log.info("'SystemUpdateID' was updated from '{}' to '{}'", oldSystemUpdateId, newSystemUpdateId);
    }

}
