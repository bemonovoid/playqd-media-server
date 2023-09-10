package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;

public interface BrowsableObjectFinder {

    BrowseResult find(BrowseContext context);

}
