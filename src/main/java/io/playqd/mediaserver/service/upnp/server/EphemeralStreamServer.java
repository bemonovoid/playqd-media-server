package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.transport.Router;
import org.jupnp.transport.spi.InitializationException;
import org.jupnp.transport.spi.StreamServer;

import java.net.InetAddress;

@Slf4j
public class EphemeralStreamServer implements StreamServer<EphemeralStreamServerConfiguration> {

    private final EphemeralStreamServerConfiguration configuration;

    public EphemeralStreamServer(EphemeralStreamServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void init(InetAddress bindAddress, Router router) throws InitializationException {
        log.info("Stream server initialized");
    }

    @Override
    public int getPort() {
        return configuration.getListenPort();
    }

    @Override
    public void stop() {
        log.info("Stream server stopped");
    }

    @Override
    public EphemeralStreamServerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void run() {
        log.info("Stream server is running ...");
    }
}
