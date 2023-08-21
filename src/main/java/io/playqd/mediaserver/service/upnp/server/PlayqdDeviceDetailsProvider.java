package io.playqd.mediaserver.service.upnp.server;

import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.ManufacturerDetails;
import org.jupnp.model.meta.ModelDetails;
import org.jupnp.model.profile.DeviceDetailsProvider;
import org.jupnp.model.profile.RemoteClientInfo;
import org.jupnp.model.types.DLNACaps;
import org.jupnp.model.types.DLNADoc;

import java.net.URI;

public class PlayqdDeviceDetailsProvider implements DeviceDetailsProvider {

    private static final String MANUFACTURER_NAME = "Playqd Media Server";
    private static final String MANUFACTURER_URL = "https://www.playqdmediaserver.com/";
    private static final ManufacturerDetails MANUFACTURER_DETAILS = new ManufacturerDetails(MANUFACTURER_NAME, MANUFACTURER_URL);
    private static final String MODEL_NUMBER = "1";
    private	static final DLNADoc[] DLNA_DOCS = new DLNADoc[] {new DLNADoc("DMS", DLNADoc.Version.V1_5), new DLNADoc("M-DMS", DLNADoc.Version.V1_5)};
    private	static final DLNACaps DLNA_CAPS = new DLNACaps(new String[] {});
    private static final DLNACaps SEC_CAP = new DLNACaps(new String[] {"smi", "DCM10", "getMediaInfo.sec", "getCaptionInfo.sec"});

    @Override
    public DeviceDetails provide(RemoteClientInfo info) {
        //find the renderer
//        UmsRemoteClientInfo umsinfo = new UmsRemoteClientInfo(info);
        var isXbox360Request = false; // umsinfo.isXbox360Request();
        String friendlyName = "Playqd Media Server";
        if (isXbox360Request) {
            friendlyName += " : Windows Media Connect";
        }
        String modelName = isXbox360Request ? "Windows Media Connect" : "Playqd Media Server";
        String modelDescription = "Playqd Media Server" + " - UPnP/AV 1.0 Compliant Media Server";
        String modelNumber = isXbox360Request ? "1" : MODEL_NUMBER;
        ModelDetails modelDetails = new ModelDetails(modelName, modelDescription, modelNumber, MANUFACTURER_URL);
        URI presentationURI = null;
        var localAddress = ""; //umsinfo.getLocalAddress()
        if (localAddress != null) {
            String webInterfaceUrl = "http://localhost:8080";
            presentationURI = URI.create(webInterfaceUrl);
        }
        return new DeviceDetails(
                null,
                friendlyName,
                MANUFACTURER_DETAILS,
                modelDetails,
                null,
                null,
                presentationURI,
                DLNA_DOCS,
                DLNA_CAPS,
                SEC_CAP
        );
    }
}
