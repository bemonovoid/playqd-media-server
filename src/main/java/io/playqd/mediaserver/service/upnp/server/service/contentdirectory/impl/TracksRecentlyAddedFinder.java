package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.metadata.ImageService;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.ObjectIdPattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
final class TracksRecentlyAddedFinder extends AbstractTracksFinder {

    TracksRecentlyAddedFinder(AudioFileDao audioFileDao,
                              ImageService imageService,
                              PlayqdProperties playqdProperties) {
        super(audioFileDao, imageService, playqdProperties);
    }

    @Override
    protected long countTotal(BrowseContext context) {
        return audioFileDao.countRecentlyAdded();
    }

    @Override
    protected String buildItemObjectId(AudioFile audioFile) {
        return ObjectIdPattern.ARTIST_ALBUM_TRACK_PATH
                .compile(audioFile.artistId(), audioFile.albumId(), audioFile.trackId());
    }

    @Override
    protected Page<AudioFile> findAudioFiles(BrowseContext context, Pageable pageable) {
        var page = audioFileDao.getRecentlyAdded(pageable);
        return new PageImpl<>(page.getContent());
    }

    @Override
    protected int calculateRequestedCount(BrowseContext context) {
        var maxDisplayedCount = playqdProperties.getUpnp().getAction().getBrowse().getMaxDisplayedRecentlyAdded();
        return maxDisplayedCount > 0 ? maxDisplayedCount : super.calculateRequestedCount(context);
    }

    @Override
    protected String getDcTitle(BrowseContext context, AudioFile audioFile) {
        return audioFile.artistName() + " - " + super.getDcTitle(context, audioFile);
    }

}
