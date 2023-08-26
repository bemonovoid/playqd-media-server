package io.playqd.mediaserver.service.upnp.server;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.StateVariables;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.StateVariableContextHolder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpnpServiceContextHolder {

    private final PlayqdUpnpServiceImpl playqdUpnpService;

    UpnpServiceContextHolder(PlayqdProperties playqdProperties, StateVariableContextHolder stateVariableContextHolder) {
        this.playqdUpnpService =
                new PlayqdUpnpServiceImpl(playqdProperties.getUpnp(), getDeviceId(stateVariableContextHolder));
    }

    public final PlayqdUpnpService getServiceInstance() {
        return this.playqdUpnpService;
    }

    @PostConstruct
    void startup() {
        this.playqdUpnpService.run();
    }

    @PreDestroy
    void terminate() {
        this.playqdUpnpService.shutdown();
    }

    private static String getDeviceId(StateVariableContextHolder stateVariableContextHolder) {
        return stateVariableContextHolder.getOrUpdate(
                StateVariables.LOCAL_DEVICE_ID, () -> UUID.randomUUID().toString());
    }
}
