package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowsableObjectSetter;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.persistence.jpa.dao.PersistedBrowsableObject;
import io.playqd.mediaserver.service.mediasource.MediaSource;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public final class MediaSourcesFinder extends AbstractFolderContentFinder {

    private final MediaSourceDao mediaSourceDao;

    public MediaSourcesFinder(MediaSourceDao mediaSourceDao, BrowsableObjectDao browsableObjectDao) {
        super(browsableObjectDao);
        this.mediaSourceDao = mediaSourceDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {

        log.info("Searching available media source(s) ...");

        List<PersistedBrowsableObject> objects = new ArrayList<>(browsableObjectDao.getRoot());

        if (objects.isEmpty()) {

            var mediaSources = mediaSourceDao.getAll();

            objects = mediaSources.stream()
                    .filter(mediaSource -> {
                        var exists = Files.exists(mediaSource.path());
                        if (!exists) {
                            log.warn("Media source (id: {}) path does not exist: {}. Skipping this media source",
                                    mediaSource.id(), mediaSource.path());
                        }
                        return exists;
                    })
                    .map(this::toPersistedObjectSetter)
                    .map(browsableObjectDao::save)
                    .toList();
        }

        log.info("Retrieved {} media source(s)", objects.size());

        var result = objects.stream().map(source -> fromPersistedObject(context.getRequest(), source)).toList();

        return new BrowseResult(objects.size(), objects.size(), result);
    }

    private Consumer<BrowsableObjectSetter> toPersistedObjectSetter(MediaSource mediaSource) {
        return toPersistedObjectSetter(mediaSource.path(), mediaSource.name());
    }

    private Consumer<BrowsableObjectSetter> toPersistedObjectSetter(Path path, String mediaSourceName) {
        return setter -> {
            var counts = countChildren(path);
            setter.setDcTitle(mediaSourceName);
            setter.setLocation(path.toString());
            setter.setUpnpClass(UpnpClass.storageFolder);
            setter.setChildCount(counts.totalCount());
            setter.setChildContainerCount(counts.childContainerCount());
        };
    }

    private static BrowsableObject fromPersistedObject(Browse browseRequest, PersistedBrowsableObject persistedObject) {
        return BrowsableObjectImpl.builder()
                .objectId(persistedObject.objectId())
                .parentObjectId(browseRequest.getObjectID())
                .childCount(persistedObject.childCount().get())
                .childContainerCount(persistedObject.childContainerCount())
                .dc(buildDcTagValues(persistedObject))
                .upnp(buildUpnpTagValues(persistedObject))
                .build();
    }

    private static DcTagValues buildDcTagValues(PersistedBrowsableObject source) {
        return DcTagValues.builder()
                .title(source.dcTitle())
                .build();
    }

    private static UpnpTagValues buildUpnpTagValues(PersistedBrowsableObject source) {
        return UpnpTagValues.builder().upnpClass(UpnpClass.storageFolder).build();
    }
}
