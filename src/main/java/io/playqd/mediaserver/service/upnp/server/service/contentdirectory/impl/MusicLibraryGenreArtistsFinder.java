package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.model.Artist;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.*;
import org.springframework.stereotype.Component;

@Component
final class MusicLibraryGenreArtistsFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;

    MusicLibraryGenreArtistsFinder(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        var genreArtists = audioFileDao.getGenreArtists(readGenreId(context));
        var result = genreArtists.stream()
                .map(artist -> buildBrowsableObject(context.getRequest(), artist))
                .toList();
        return new BrowseResult(result.size(), result.size(), result);
    }

    private static String readGenreId(BrowseContext context) {
        return context.getRequiredHeader(BrowseContext.HEADER_GENRE_ID, String.class);
    }

    private BrowsableObject buildBrowsableObject(Browse browseRequest, Artist artist) {
        return BrowsableObjectImpl.builder()
                .objectId(buildArtistObjectId(artist))
                .parentObjectId(browseRequest.getObjectID())
                .childCount(artist.albumsCount())
                .childContainerCount(artist.albumsCount())
                .dc(buildDcTagValues(artist))
                .upnp(buildUpnpTagValues(artist))
                .build();
    }

    private static String buildArtistObjectId(Artist artist) {
        return ObjectIdPattern.ARTIST_ALBUMS_PATH.compile(artist.id());
    }

    private static DcTagValues buildDcTagValues(Artist artist) {
        return DcTagValues.builder().title(artist.name()).build();
    }

    private UpnpTagValues buildUpnpTagValues(Artist artist) {
        return UpnpTagValues.builder().upnpClass(UpnpClass.musicArtist).build();
    }
}
