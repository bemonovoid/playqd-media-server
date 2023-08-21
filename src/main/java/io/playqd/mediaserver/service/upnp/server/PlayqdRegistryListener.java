package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.model.meta.RemoteService;
import org.jupnp.registry.DefaultRegistryListener;
import org.jupnp.registry.Registry;

@Slf4j
public class PlayqdRegistryListener extends DefaultRegistryListener {

	private static final String AV_TRANSPORT_SERVICE = "AVTransport";
	private static final String RENDERING_CONTROL_SERVICE = "RenderingControl";

	public static final int AVT = 1;
	public static final int RC = 2;

	@Override
	public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
		if (device != null && DeviceTypes.isMediaRenderer(device)) {
			int ctrl = 0;
			for (RemoteService remoteService : device.getServices()) {
				String sid = remoteService.getServiceId().getId();
				log.debug("Subscribing to " + sid + " service on " + device.getDisplayString());
				if (sid.contains(AV_TRANSPORT_SERVICE)) {
					ctrl |= AVT;
				}
//				else if (sid.contains(RENDERING_CONTROL_SERVICE)) {
//					ctrl |= RC;
//				}
				registry.getUpnpService().getControlPoint().execute(new ServiceEventSubscriptionCallback(remoteService));
			}
		}
		log.debug("Remote device added: {}", device.getDisplayString());
	}

	@Override
	public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
		super.remoteDeviceRemoved(registry, device);
	}

	@Override
	public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
		log.debug("Remote device updated: {}", device.getDisplayString());
	}
}
