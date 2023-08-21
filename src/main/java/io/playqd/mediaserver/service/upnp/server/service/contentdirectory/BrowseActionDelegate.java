package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;

public interface BrowseActionDelegate {

    BrowseResult browseRoots(BrowseContext browseActionContext);

    BrowseResult browseChildren(BrowseContext browseContext);

}
