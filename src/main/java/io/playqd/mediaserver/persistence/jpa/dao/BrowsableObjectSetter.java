package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.UpnpClass;

public interface BrowsableObjectSetter {

    void setLocation(String location);

    void setChildrenCountTransient(long count);

    void setParentId(Long parentId);

    void setDcTitle(String dcTitle);

    void setUpnpClass(UpnpClass upnpClass);
}
