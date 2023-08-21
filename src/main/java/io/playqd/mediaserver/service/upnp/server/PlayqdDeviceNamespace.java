package io.playqd.mediaserver.service.upnp.server;

import org.jupnp.model.Namespace;
import org.jupnp.model.meta.Service;

import java.net.URI;

public final class PlayqdDeviceNamespace extends Namespace {

    @Override
    public URI getControlPath(Service service) {
        var servicePath = super.getControlPath(service);
        return URI.create(servicePath.getPath().replaceFirst("dev", "ws"));
    }
}
