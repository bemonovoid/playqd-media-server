package io.playqd.mediaserver.service.upnp.server;

import io.playqd.mediaserver.config.properties.UpnpStreamServerProperties;
import org.jupnp.transport.spi.StreamServerConfiguration;

public class EphemeralStreamServerConfiguration implements StreamServerConfiguration {

    private final int port;

    public EphemeralStreamServerConfiguration(UpnpStreamServerProperties properties) {
        this.port = properties.getPort();
    }

    @Override
    public int getListenPort() {
        return port;
    }
}
