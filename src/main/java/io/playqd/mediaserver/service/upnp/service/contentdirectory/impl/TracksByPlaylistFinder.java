package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.metadata.ImageService;
import io.playqd.mediaserver.service.playlist.PlaylistService;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ObjectIdPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Slf4j
public class TracksByPlaylistFinder extends AbstractTracksFinder {

    private final PlaylistService playlistService;

    public TracksByPlaylistFinder(PlayqdProperties playqdProperties,
                                  AudioFileDao audioFileDao,
                                  ImageService imageService,
                                  PlaylistService playlistService) {
        super(playqdProperties, audioFileDao, imageService);
        this.playlistService = playlistService;
    }

    @Override
    protected long countTotal(BrowseContext context) {
        return playlistService.getPlaylistAudioFiles(readPlaylistId(context)).size();
    }

    @Override
    protected String buildItemObjectId(AudioFile audioFile) {
        return ObjectIdPattern.ARTIST_ALBUM_TRACK_PATH
                .compile(audioFile.artistId(), audioFile.albumId(), audioFile.trackId());
    }

    @Override
    protected Page<AudioFile> findAudioFiles(BrowseContext context, Pageable pageable) {
        var audioFiles = playlistService.getPlaylistAudioFiles(readPlaylistId(context));
        return new PageImpl<>(audioFiles);
    }

    @Override
    protected String getDcTitle(BrowseContext context, AudioFile audioFile) {
        return audioFile.artistName() + " - " + super.getDcTitle(context, audioFile);
    }

    private static String readPlaylistId(BrowseContext context) {
        return context.getRequiredHeader(BrowseContext.HEADER_PLAYLIST_ID);
    }
}
