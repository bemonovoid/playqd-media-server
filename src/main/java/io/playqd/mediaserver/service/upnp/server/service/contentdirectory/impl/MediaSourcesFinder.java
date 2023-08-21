package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowsableObjectSetter;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.persistence.jpa.dao.PersistedBrowsableObject;
import io.playqd.mediaserver.service.mediasource.MediaSource;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
@Component
final class MediaSourcesFinder implements BrowsableObjectFinder {

    private final MediaSourceDao mediaSourceDao;
    private final BrowsableObjectDao browsableObjectDao;

    public MediaSourcesFinder(MediaSourceDao mediaSourceDao, BrowsableObjectDao browsableObjectDao) {
        this.mediaSourceDao = mediaSourceDao;
        this.browsableObjectDao = browsableObjectDao;
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
                    .map(this::createBrowsableObjectSetter)
                    .map(browsableObjectDao::save)
                    .toList();
        }

        log.info("Retrieved {} media source(s)", objects.size());

        var result = objects.stream().map(source -> fromSource(context.getRequest(), source)).toList();

        return new BrowseResult(objects.size(), objects.size(), result);
    }

    private Consumer<BrowsableObjectSetter> createBrowsableObjectSetter(MediaSource mediaSource) {
        return createBrowsableObjectSetter(mediaSource.path(), mediaSource.name());
    }

    private Consumer<BrowsableObjectSetter> createBrowsableObjectSetter(Path path, String mediaSourceName) {
        return setter -> {
            setter.setDcTitle(mediaSourceName);
            setter.setLocation(path.toString());
            setter.setChildrenCountTransient(countChildren(path));
        };
    }

    private static long countChildren(Path path) {
        try (Stream<Path> dirItems = Files.list(path)) {
            return dirItems.count();
        } catch (IOException e) {
            throw new PlayqdException(String.format("Failed counting media source children at %s", path), e);
        }
    }

    private static BrowsableObject fromSource(Browse browseRequest, PersistedBrowsableObject source) {
        return BrowsableObjectImpl.builder()
                .objectId(source.objectId())
                .parentObjectId(browseRequest.getObjectID())
                .childCount(source.childrenCount().get())
                .dc(buildDcTagValues(source))
                .upnp(buildUpnpTagValues(source))
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
