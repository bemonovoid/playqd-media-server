package io.playqd.mediaserver.service.mediasource;

import io.playqd.mediaserver.model.event.AudioFileByteStreamRequestedEvent;
import io.playqd.mediaserver.model.event.MediaSourceContentChangedEvent;
import io.playqd.mediaserver.persistence.AudioFileDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
class MediaSourceEventListener {

    private final AudioFileDao audioFileDao;
    private final MediaSourceScannerService mediaSourceScannerService;

    MediaSourceEventListener(AudioFileDao audioFileDao,
                             MediaSourceScannerService mediaSourceScannerService) {
        this.audioFileDao = audioFileDao;
        this.mediaSourceScannerService = mediaSourceScannerService;
    }

    @EventListener(MediaSourceContentChangedEvent.class)
    public void handleMediaSourceContentChangedEvent(MediaSourceContentChangedEvent event) {
        event.changedContentDirs().forEach(path -> mediaSourceScannerService.scan(event.mediaSource().id(), path));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener(AudioFileByteStreamRequestedEvent.class)
    public void handleMediaSourceContentStreamRequested(AudioFileByteStreamRequestedEvent event) {
        audioFileDao.updateAudioFileLastPlaybackDate(event.audioFileId());
    }

}
