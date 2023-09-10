package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.model.Genre;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ObjectIdPattern;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;

public final class GenresFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;

    public GenresFinder(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        var browseRequest = context.getRequest();
        var startingIndex = browseRequest.getStartingIndex();
        var requestedCount = browseRequest.getRequestedCount();
        var totalCount = (int) audioFileDao.countGenres();

        if (startingIndex > totalCount) {
            return BrowseResult.empty();
        }

        if (startingIndex > 0) {
            var genres = audioFileDao.getAllGenres().stream()
                    .map(genre -> buildBrowsableObject(context, genre))
                    .toList();
            var offset = genres.stream().skip(startingIndex).toList();
            var result = offset.stream().limit(requestedCount).toList();
            return new BrowseResult(offset.size(), result.size(), result);
        } else {
            var genres = audioFileDao.getAllGenres().stream()
                    .map(genre -> buildBrowsableObject(context, genre))
                    .limit(requestedCount)
                    .toList();
            return new BrowseResult(totalCount, genres.size(), genres);
        }
    }

    private static BrowsableObject buildBrowsableObject(BrowseContext context, Genre genre) {
        var childCount = countChild(context, genre);
        return BrowsableObjectImpl.builder()
                .objectId(buildGenreObjectId(context, genre))
                .parentObjectId(context.getRequest().getObjectID())
                .childCount(childCount)
                .childContainerCount(childCount)
                .dc(buildDcTagValues(genre))
                .upnp(buildUpnpTagValues(genre))
                .build();
    }

    private static String buildGenreObjectId(BrowseContext context, Genre genre) {
        return context.getRequiredHeader(BrowseContext.HEADER_OBJECT_ID_PATTERN, ObjectIdPattern.class)
                .compile(genre.id());
    }

    private static DcTagValues buildDcTagValues(Genre genre) {
        return DcTagValues.builder().title(genre.name()).build();
    }

    private static UpnpTagValues buildUpnpTagValues(Genre genre) {
        return UpnpTagValues.builder()
                .genre(genre.name())
                .upnpClass(UpnpClass.musicGenre)
                .build();
    }

    private static long countChild(BrowseContext context, Genre genre) {
        switch (context.getRequiredHeader(BrowseContext.HEADER_OBJECT_ID_PATTERN, ObjectIdPattern.class)) {
            case GENRE_ARTISTS_PATH -> {
                return genre.artistCount();
            }
            case GENRE_ALBUMS_PATH -> {
                return genre.albumCount();
            }
            default -> {
                return 0;
            }
        }
    }
}
