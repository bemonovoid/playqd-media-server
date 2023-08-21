package io.playqd.mediaserver.service.upnp.server;

import lombok.Getter;
import org.jupnp.model.meta.Device;
import org.jupnp.model.types.DeviceType;
import org.jupnp.model.types.UDADeviceType;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DeviceTypes {

    MEDIA_RENDERER("MediaRenderer", new UDADeviceType("MediaRenderer", 1));

    private final String typeName;
    private final DeviceType deviceType;

    DeviceTypes(String typeName, DeviceType deviceType) {
        this.typeName = typeName;
        this.deviceType = deviceType;
    }

    public static List<DeviceType> allTypes() {
        return Arrays.stream(DeviceTypes.values()).map(DeviceTypes::getDeviceType).toList();
    }

    public static boolean isMediaRenderer(Device<?, ?, ?> device) {
        return MEDIA_RENDERER.typeName.equals(device.getType().getType());
    }
}
