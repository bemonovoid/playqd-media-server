package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.metadata.AlbumArtService;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.ObjectIdPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
final class MusicLibraryArtistAlbumTracksFinder extends AbstractTracksFinder {

    MusicLibraryArtistAlbumTracksFinder(AudioFileDao audioFileDao,
                                        AlbumArtService albumArtService,
                                        PlayqdProperties playqdProperties) {
        super(audioFileDao, albumArtService, playqdProperties);
    }

    @Override
    protected long countTotal(BrowseContext context) {
        return audioFileDao.countAlbumAudioFiles(readAlbumId(context));
    }

    @Override
    protected String buildItemObjectId(AudioFile audioFile) {
        return ObjectIdPattern.ARTIST_ALBUM_TRACK_PATH.compile(
                audioFile.artistId(), audioFile.albumId(), audioFile.trackId());
    }

    @Override
    protected Page<AudioFile> findAudioFiles(BrowseContext context, Pageable pageable) {
        return new PageImpl<>(audioFileDao.getAudioFilesByAlbumId(readAlbumId(context)));
    }

    private static String readAlbumId(BrowseContext context) {
        return context.getRequiredHeader(BrowseContext.HEADER_ALBUM_ID, String.class);
    }

}
