package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.model.Album;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ObjectIdPattern;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AlbumsByArtistFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;

    public AlbumsByArtistFinder(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        var artistAlbums = audioFileDao.getArtistAlbums(readArtistId(context));
        var result = artistAlbums.stream()
                .map(album -> buildBrowsableObject(context.getRequest(), album))
                .toList();
        return new BrowseResult(result.size(), result.size(), result);
    }

    private static String readArtistId(BrowseContext context) {
        return context.getRequiredHeader(BrowseContext.HEADER_ARTIST_ID, String.class);
    }

    private BrowsableObject buildBrowsableObject(Browse browseRequest, Album album) {
        return BrowsableObjectImpl.builder()
                .objectId(buildAlbumObjectId(album))
                .parentObjectId(browseRequest.getObjectID())
                .childCount(album.tracksCount())
                .dc(buildDcTagValues(album))
                .upnp(buildUpnpTagValues(album))
                .build();
    }

    private static String buildAlbumObjectId(Album album) {
        return ObjectIdPattern.ARTIST_ALBUM_TRACKS_PATH.compile(album.artistId(), album.id());
    }

    private static DcTagValues buildDcTagValues(Album album) {
        return DcTagValues.builder()
                .title(album.name())
                .creator(album.artistName())
                .contributor(album.artistName())
                .build();
    }

    private UpnpTagValues buildUpnpTagValues(Album album) {
        return UpnpTagValues.builder()
                .upnpClass(UpnpClass.musicAlbum)
                .artist(album.artistName())
                .genre(album.genre())
                .build();
    }

}
