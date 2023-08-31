package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.UpnpClass;

import java.nio.file.Path;
import java.util.function.Supplier;

public record PersistedBrowsableObject(long id,
                                       Long parentId,
                                       String objectId,
                                       String dcTitle,
                                       Path location,
                                       UpnpClass upnpClass,
                                       Supplier<Long> childrenCount) {
}
