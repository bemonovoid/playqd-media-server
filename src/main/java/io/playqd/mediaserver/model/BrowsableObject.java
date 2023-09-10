package io.playqd.mediaserver.model;

import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ResTag;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;

import java.util.Collections;
import java.util.List;

public interface BrowsableObject {

    Long getId();

    String getObjectId();

    Long getParentId();

    boolean hasChildren();

    long getChildCount();

    long getChildContainerCount();

    DcTagValues getDc();

    UpnpTagValues getUpnp();

    default List<ResTag> getResources() {
        return Collections.emptyList();
    }

    default boolean isSearchable() {
        return false;
    }

    default boolean isRestricted() {
        return true;
    }

    default boolean isRoot() {
        return getParentId() == null;
    }

    default int getSortOrderId() {
        return 0;
    }

    default boolean isVirtual() {
        return false;
    }

    default boolean isVirtualRoot() {
        return isRoot() && isVirtual();
    }

    default String getParentObjectId() {
        return null;
    }
}
