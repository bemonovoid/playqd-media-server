package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.service.upnp.service.ActionResponse;

import java.util.Collections;
import java.util.List;

public record BrowseResult(long totalMatches, int numberReturned, List<BrowsableObject> objects)
        implements ActionResponse {

    public static BrowseResult empty() {
        return new BrowseResult(0, 0, Collections.emptyList());
    }

}
