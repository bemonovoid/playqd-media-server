package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.metadata.AlbumArtService;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.ObjectIdPattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class MusicLibraryArtistTracksFinder extends AbstractTracksFinder {

    MusicLibraryArtistTracksFinder(AudioFileDao audioFileDao,
                                   AlbumArtService albumArtService,
                                   PlayqdProperties playqdProperties) {
        super(audioFileDao, albumArtService, playqdProperties);
    }

    @Override
    protected long countTotal(BrowseContext context) {
        return audioFileDao.countArtistAudioFiles(readArtistId(context));
    }

    @Override
    protected String buildItemObjectId(AudioFile audioFile) {
        return ObjectIdPattern.ARTIST_TRACK_PATH.compile(audioFile.artistId(), audioFile.trackId());
    }

    @Override
    protected Page<AudioFile> findAudioFiles(BrowseContext context, Pageable pageable) {
        return new PageImpl<>(audioFileDao.getAudioFilesByArtistId(readArtistId(context)));
    }

    private static String readArtistId(BrowseContext context) {
        return context.getRequiredHeader(BrowseContext.HEADER_ARTIST_ID, String.class);
    }

}
