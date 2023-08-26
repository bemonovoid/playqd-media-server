package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.binding.annotations.AnnotationLocalServiceBinder;
import org.jupnp.model.DefaultServiceManager;
import org.jupnp.model.ValidationException;
import org.jupnp.model.meta.*;
import org.jupnp.model.profile.DeviceDetailsProvider;
import org.jupnp.model.profile.RemoteClientInfo;
import org.jupnp.model.types.UDADeviceType;
import org.jupnp.model.types.UDN;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PlayqdLocalDevice extends LocalDevice {

    private final DeviceDetailsProvider deviceDetailsProvider;

    public PlayqdLocalDevice(String deviceId) throws ValidationException {
        super(
                new DeviceIdentity(new UDN(deviceId)),
                new UDADeviceType("MediaServer"),
                null,
                new Icon[]{},
                createMediaServerServices(),
                null
        );
        this.deviceDetailsProvider = new PlayqdDeviceDetailsProvider();
    }

    @Override
    public DeviceDetails getDetails(RemoteClientInfo info) {
        if (deviceDetailsProvider != null) {
            return deviceDetailsProvider.provide(info);
        }
        return this.getDetails();
    }

    public static LocalDevice createMediaServerDevice(String deviceId) {
        try {
            return new PlayqdLocalDevice(deviceId);
        } catch (ValidationException e) {
            log.error("Error in upnp local device creation", e);
            return null;
        }
    }

    private static LocalService<?>[] createMediaServerServices() {
        List<LocalService<?>> services = new ArrayList<>();
        services.add(createContentDirectoryService());
        services.add(createServerConnectionManagerService());
        services.add(createMediaReceiverRegistrarService());
        return services.toArray(LocalService[]::new);
    }

    /**
     * Creates the upnp ContentDirectoryService.
     * @return The ContenDirectoryService.
     */
    private static LocalService<UpnpContentDirectoryServiceImpl> createContentDirectoryService() {
        LocalService<UpnpContentDirectoryServiceImpl> contentDirectoryService = new AnnotationLocalServiceBinder().read(UpnpContentDirectoryServiceImpl.class);
        contentDirectoryService.setManager(new DefaultServiceManager<>(contentDirectoryService, null) {

            @Override
            protected int getLockTimeoutMillis() {
                return 1000;
            }

            @Override
            protected UpnpContentDirectoryServiceImpl createServiceInstance() throws Exception {
                return new UpnpContentDirectoryServiceImpl();
            }
        });
        return contentDirectoryService;
    }

    /**
     * creates the upnp ConnectionManagerService.
     *
     * @return the service
     */
    private static LocalService<UpnpConnectionManagerServiceImpl> createServerConnectionManagerService() {
        LocalService<UpnpConnectionManagerServiceImpl> connectionManagerService = new AnnotationLocalServiceBinder().read(UpnpConnectionManagerServiceImpl.class);
        connectionManagerService.setManager(new DefaultServiceManager<>(connectionManagerService, UpnpConnectionManagerServiceImpl.class) {

            @Override
            protected int getLockTimeoutMillis() {
                return 1000;
            }

            @Override
            protected UpnpConnectionManagerServiceImpl createServiceInstance() throws Exception {
                return new UpnpConnectionManagerServiceImpl();
            }
        });

        return connectionManagerService;
    }

    /**
     * creates the upnp MediaReceiverRegistrarService.
     *
     * @return the service
     */
    private static LocalService<PlayqdMediaReceiverRegistrarService> createMediaReceiverRegistrarService() {
        LocalService<PlayqdMediaReceiverRegistrarService> mediaReceiverRegistrarService =
                new AnnotationLocalServiceBinder().read(PlayqdMediaReceiverRegistrarService.class);
        mediaReceiverRegistrarService.setManager(new DefaultServiceManager<>(mediaReceiverRegistrarService, null) {
            @Override
            protected int getLockTimeoutMillis() {
                return 1000;
            }

            @Override
            protected PlayqdMediaReceiverRegistrarService createServiceInstance() throws Exception {
                return new PlayqdMediaReceiverRegistrarService();
            }
        });
        return mediaReceiverRegistrarService;
    }
}
