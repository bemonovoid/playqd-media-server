package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class RootContainersFinder implements BrowsableObjectFinder {

    private final MediaSourceDao mediaSourceDao;

    RootContainersFinder(MediaSourceDao mediaSourceDao) {
        this.mediaSourceDao = mediaSourceDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        return new BrowseResult(
                2, 2, List.of(buildFoldersRoot(), buildMusicLibraryRoot()));
    }

    private BrowsableObject buildFoldersRoot() {
        return BrowsableObjectImpl.builder()
                .objectId(SystemContainerName.ROOT_FOLDERS.getObjectId())
                .parentObjectId("-1")
                .childCount(mediaSourceDao.getAll().size())
                .sortOrderId(SystemContainerName.ROOT_FOLDERS.getOrderId())
                .dc(buildDcTagValues(SystemContainerName.ROOT_FOLDERS))
                .upnp(buildUpnpTagValues())
                .build();
    }

    private BrowsableObject buildMusicLibraryRoot() {
        return BrowsableObjectImpl.builder()
                .objectId(SystemContainerName.ROOT_MUSIC_LIBRARY.getObjectId())
                .parentObjectId("-1")
                .sortOrderId(SystemContainerName.ROOT_MUSIC_LIBRARY.getOrderId())
                .childCount(SystemContainerName.getMusicLibraryChildren().size())
                .dc(buildDcTagValues(SystemContainerName.ROOT_MUSIC_LIBRARY))
                .upnp(buildUpnpTagValues())
                .build();
    }

    private static DcTagValues buildDcTagValues(SystemContainerName virtualContainer) {
        return DcTagValues.builder()
                .title(virtualContainer.getDcTitleName())
                .build();
    }

    private static UpnpTagValues buildUpnpTagValues() {
        return UpnpTagValues.builder().upnpClass(UpnpClass.storageFolder).build();
    }
}
