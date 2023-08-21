package io.playqd.mediaserver.service.upnp.server;

import org.jupnp.UpnpService;
import org.jupnp.model.meta.LocalDevice;

public interface PlayqdUpnpService extends UpnpService  {

    LocalDevice getThisLocalDevice();

}
