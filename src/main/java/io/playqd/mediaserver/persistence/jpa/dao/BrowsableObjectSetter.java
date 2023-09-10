package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;

public interface BrowsableObjectSetter {

    void setLocation(String location);

    void setChildCount(long count);

    void setChildContainerCount(long count);

    void setParentId(Long parentId);

    void setDcTitle(String dcTitle);

    void setUpnpClass(UpnpClass upnpClass);
}
