package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.model.Artist;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ObjectIdPattern;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;
import org.springframework.data.domain.Pageable;

public final class ArtistsFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;

    public ArtistsFinder(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        var browseRequest = context.getRequest();
        var startingIndex = browseRequest.getStartingIndex();
        var requestedCount = browseRequest.getRequestedCount();
        var totalCount = (int) audioFileDao.countArtists();

        if (startingIndex > totalCount) {
            return BrowseResult.empty();
        }

        if (startingIndex > 0) {
            var artists = audioFileDao.getArtists(Pageable.unpaged()).stream()
                    .map(artist -> buildBrowsableObject(context, artist))
                    .toList();
            var offset = artists.stream().skip(startingIndex).toList();
            var result = offset.stream().limit(requestedCount).toList();
            return new BrowseResult(offset.size(), result.size(), result);
        } else {
            var artists = audioFileDao.getArtists(Pageable.unpaged()).stream()
                    .map(artist -> buildBrowsableObject(context, artist))
                    .limit(requestedCount)
                    .toList();
            return new BrowseResult(totalCount, artists.size(), artists);
        }
    }

    private static BrowsableObject buildBrowsableObject(BrowseContext context, Artist artist) {
        return BrowsableObjectImpl.builder()
                .objectId(buildArtistObjectId(context, artist))
                .parentObjectId(context.getRequest().getObjectID())
                .childCount(artist.albumsCount())
                .childContainerCount(artist.albumsCount())
                .dc(buildDcTagValues(artist))
                .upnp(buildUpnpTagValues(artist))
                .build();
    }

    private static String buildArtistObjectId(BrowseContext context, Artist artist) {
        var objectIdPattern =
                context.getRequiredHeader(BrowseContext.HEADER_OBJECT_ID_PATTERN, ObjectIdPattern.class);
        return objectIdPattern.compile(artist.id());
    }

    private static DcTagValues buildDcTagValues(Artist artist) {
        return DcTagValues.builder().title(artist.name()).build();
    }

    private static UpnpTagValues buildUpnpTagValues(Artist artist) {
        return UpnpTagValues.builder().upnpClass(UpnpClass.musicArtist).build();
    }

}
