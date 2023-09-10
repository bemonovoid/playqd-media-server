package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.entity.AudioFileJpaEntity;
import io.playqd.mediaserver.service.metadata.ImageService;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ObjectIdPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
public final class TracksMostPlayedFinder extends AbstractTracksFinder {

    public TracksMostPlayedFinder(PlayqdProperties playqdProperties,
                                  AudioFileDao audioFileDao,
                                  ImageService imageService) {
        super(playqdProperties, audioFileDao, imageService);
    }

    @Override
    protected long countTotal(BrowseContext context) {
        return audioFileDao.countPlayed();
    }

    @Override
    protected String buildItemObjectId(AudioFile audioFile) {
        return ObjectIdPattern.ARTIST_ALBUM_TRACK_PATH
                .compile(audioFile.artistId(), audioFile.albumId(), audioFile.trackId());
    }

    @Override
    protected Page<AudioFile> findAudioFiles(BrowseContext context, Pageable pageable) {
        var sort = Sort.by(AudioFileJpaEntity.FLD_FILE_PLAYBACK_COUNT).descending();
        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        var page = audioFileDao.getPlayed(sortedPageable);
        return new PageImpl<>(page.getContent());
    }

    @Override
    protected int calculateRequestedCount(BrowseContext context) {
        var maxDisplayedCount = playqdProperties.getUpnp().getAction().getBrowse().getMaxDisplayedMostPlayed();
        return maxDisplayedCount > 0 ? maxDisplayedCount : super.calculateRequestedCount(context);
    }

    @Override
    protected String getDcTitle(BrowseContext context, AudioFile audioFile) {
        return String.format("(%s) %s - %s",
                audioFile.playbackCount(), audioFile.artistName(),super.getDcTitle(context, audioFile));
    }
}
