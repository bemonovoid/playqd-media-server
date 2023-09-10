package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.SystemContainerName;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;

import java.util.Comparator;

public final class MusicLibraryChildrenFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;

    public MusicLibraryChildrenFinder(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        var result = SystemContainerName.getMusicLibraryChildren().stream()
                .map(container -> buildContainer(context.getRequest(), container))
                .sorted(Comparator.comparing(BrowsableObject::getSortOrderId))
                .toList();
        return new BrowseResult(result.size(), result.size(), result);
    }

    private BrowsableObject buildContainer(Browse browseRequest, SystemContainerName container) {
        var count = countChildren(container);
        return BrowsableObjectImpl.builder()
                .objectId(container.getObjectId())
                .parentObjectId(browseRequest.getObjectID())
                .childCount(count)
                .childContainerCount(countChildContainers(count, container))
                .sortOrderId(container.getOrderId())
                .dc(buildDcTagValues(container))
                .upnp(buildUpnpTagValues(container))
                .build();
    }

    private long countChildren(SystemContainerName container) {
        switch (container) {
            case ARTIST_ALBUM, ARTIST_TRACK -> {
                return audioFileDao.countArtists();
            }
            case GENRE_ARTIST, GENRE_ALBUM -> {
                return audioFileDao.countGenres();
            }
            case TRACKS_MOST_PLAYED, TRACKS_RECENTLY_PLAYED -> {
                return audioFileDao.countPlayed();
            }
            case TRACKS_RECENTLY_ADDED -> {
                return audioFileDao.countRecentlyAdded();
            }
            default -> {
                return 0;
            }
        }
    }

    private static long countChildContainers(long childCount, SystemContainerName container) {
        switch (container) {
            case ARTIST_ALBUM, ARTIST_TRACK, GENRE_ARTIST, GENRE_ALBUM -> {
                return childCount;
            }
            default -> {
                return 0;
            }
        }
    }

    private static DcTagValues buildDcTagValues(SystemContainerName container) {
        return DcTagValues.builder()
                .title(container.getDcTitleName())
                .build();
    }

    private static UpnpTagValues buildUpnpTagValues(SystemContainerName container) {
        return UpnpTagValues.builder().upnpClass(UpnpClass.storageFolder).build();
    }
}
