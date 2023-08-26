package io.playqd.mediaserver.service.upnp.server;

import io.playqd.mediaserver.config.properties.UpnpServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.UpnpServiceImpl;
import org.jupnp.model.message.header.DeviceTypeHeader;
import org.jupnp.model.meta.LocalDevice;
import org.jupnp.model.types.DeviceType;
import org.jupnp.protocol.ProtocolFactory;
import org.jupnp.registry.Registry;
import org.jupnp.util.SpecificationViolationReporter;

@Slf4j
class PlayqdUpnpServiceImpl extends UpnpServiceImpl implements PlayqdUpnpService {

    private final LocalDevice mediaServerDevice;

    static {
        NetworkConfiguration.start();
    }

    public PlayqdUpnpServiceImpl(UpnpServiceProperties upnpServiceProperties, String deviceId) {
        super(new PlayqdUpnpServiceConfiguration(upnpServiceProperties));
        this.mediaServerDevice = PlayqdLocalDevice.createMediaServerDevice(deviceId);
        SpecificationViolationReporter.disableReporting();
    }

    @Override
    public LocalDevice getThisLocalDevice() {
        return this.mediaServerDevice;
    }

    @Override
    protected Registry createRegistry(ProtocolFactory protocolFactory) {
        var result = super.createRegistry(protocolFactory);
        result.addListener(new PlayqdRegistryListener());
        result.addDevice(this.mediaServerDevice);
        return result;
    }

    void run() {
        start();
        sendAliveNotification();
        sendSearchAll();
    }

    private void start() {
        this.startup();
    }

    private void sendAliveNotification() {
        this.getProtocolFactory().createSendingNotificationAlive(this.mediaServerDevice).run();
    }

    private void sendSearchAll() {
        for (DeviceType t : DeviceTypes.allTypes()) {
            this.getControlPoint().search(new DeviceTypeHeader(t));
        }
        log.debug("UPnP (JUPnP) services are online, listening for media renderers");
    }
}
