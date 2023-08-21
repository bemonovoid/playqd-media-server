package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.model.Album;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowseContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
abstract class AbstractAlbumsFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;

    AbstractAlbumsFinder(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    protected abstract long countTotal(BrowseContext context);

    protected abstract String buildItemObjectId(Album album);

    protected abstract List<Album> findAlbums(BrowseContext context);

    protected static String readGenreId(BrowseContext context) {
        return context.getRequiredHeader(BrowseContext.HEADER_GENRE_ID, String.class);
    }
}
