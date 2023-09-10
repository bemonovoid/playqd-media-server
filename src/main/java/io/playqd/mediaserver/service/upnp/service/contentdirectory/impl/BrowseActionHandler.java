package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.service.UpnpActionHandler;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseActionDelegate;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BrowseActionHandler implements UpnpActionHandler<BrowseContext, BrowseResult> {

    private final BrowseActionDelegate browseActionDelegate;

    public BrowseActionHandler(BrowseActionDelegate browseActionDelegate) {
        this.browseActionDelegate = browseActionDelegate;
    }

    @Override
    public BrowseResult handle(BrowseContext context) {
        var objectId = context.getRequest().getObjectID();

        log.info("Handling browse request for object id: {}", objectId);

        var browseResult = BrowseResult.empty();
        if (objectId.equals("0") || objectId.equals("-1")) {
            browseResult = browseActionDelegate.browseRoots(context);
        } else {
            browseResult = browseActionDelegate.browseChildren(context);
        }

        log.info("Total matches: {}, number returned: {}, actual objects count: {}",
                browseResult.totalMatches(), browseResult.numberReturned(), browseResult.objects().size());

        return browseResult;
    }

}