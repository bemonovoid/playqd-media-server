package io.playqd.mediaserver.service.upnp.server;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.service.upnp.server.service.StateVariableName;
import io.playqd.mediaserver.service.upnp.server.service.StateVariableContextHolder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
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
        try {
            this.playqdUpnpService.shutdown();
        } catch (Exception e) {
            log.warn("Error shutting down UpnpServer", e);
        }
    }

    private static String getDeviceId(StateVariableContextHolder stateVariableContextHolder) {
        return stateVariableContextHolder.getOrUpdate(
                StateVariableName.LOCAL_DEVICE_ID, () -> UUID.randomUUID().toString());
    }
}
