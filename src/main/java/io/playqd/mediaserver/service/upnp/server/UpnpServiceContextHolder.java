package io.playqd.mediaserver.service.upnp.server;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class UpnpServiceContextHolder {

    private final PlayqdUpnpServiceImpl playqdUpnpService;

    UpnpServiceContextHolder(PlayqdProperties playqdProperties) {
        this.playqdUpnpService = new PlayqdUpnpServiceImpl(playqdProperties.getUpnp());
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
}
