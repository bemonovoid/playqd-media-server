package io.playqd.mediaserver.service.upnp.server;

import lombok.extern.slf4j.Slf4j;
import org.jupnp.controlpoint.SubscriptionCallback;
import org.jupnp.model.gena.CancelReason;
import org.jupnp.model.gena.GENASubscription;
import org.jupnp.model.message.UpnpResponse;
import org.jupnp.model.meta.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class ServiceEventSubscriptionCallback extends SubscriptionCallback {

    private final String uuid;

    public ServiceEventSubscriptionCallback(Service s) {
        super(s);
        uuid = s.getDevice().getIdentity().getUdn().toString();
    }

    @Override
    public void eventReceived(GENASubscription subscription) {
//        JUPnPDeviceHelper.markRenderer(uuid, JUPnPDeviceHelper.ACTIVE, true);
        log.info("<<<<<<<" + subscription.getCurrentValues().toString());
        if (subscription.getCurrentValues().containsKey("LastChange")) {
//            JUPnPDeviceHelper.xml2d(uuid, subscription.getCurrentValues().get("LastChange").toString(), null);
        }
    }

    @Override
    public void established(GENASubscription sub) {
        log.debug("Subscription established: {}", sub.getService().getServiceId().getId());
    }

    @Override
    public void failed(GENASubscription sub, UpnpResponse response, Exception ex, String defaultMsg) {
        log.debug("Subscription failed: {} : {}",
                sub.getService().getServiceId().getId(),
                defaultMsg.split(": ", 2)[1]
        );
    }

    @Override
    public void failed(GENASubscription sub, UpnpResponse response, Exception ex) {
        log.debug("Subscription failed: {} : {}",
                sub.getService().getServiceId().getId(),
                SubscriptionCallback.createDefaultFailureMessage(response, ex).split(": ", 2)[1]
        );
    }

    @Override
    public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
        // Reason should be null, or it didn't end regularly
        if (reason != null) {
            log.debug("Subscription cancelled: {} on {}: {}",
                    sub.getService().getServiceId().getId(),
                    uuid,
                    reason
            );
        }
    }

    @Override
    public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
        log.debug("Missed events: {} for subscription {}",
                numberOfMissedEvents,
                sub.getService().getServiceId().getId());
    }

}
