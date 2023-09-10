package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ContentDirectoryObject {

    private final String objectId;
    private final String parentObjectId;
    private final long childCount;

    private final boolean restricted;
    private final boolean searchable;

    private final DcTagValues dc;
    private final UpnpTagValues upnp;

    private final List<ResTag> resources;
}
