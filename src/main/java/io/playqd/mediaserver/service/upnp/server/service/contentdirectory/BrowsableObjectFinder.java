package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;

public interface BrowsableObjectFinder {

    BrowseResult find(BrowseContext context);

}
