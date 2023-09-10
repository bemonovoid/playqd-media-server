package io.playqd.mediaserver.config.lifecycle;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.service.mediasource.MediaSource;
import io.playqd.mediaserver.service.mediasource.MediaSourceScanner;
import io.playqd.mediaserver.service.mediasource.MediaSourceService;
import io.playqd.mediaserver.service.mediasource.MediaSourceWatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MediaSourceInitializer implements ApplicationRunner {

    private final PlayqdProperties playqdProperties;
    private final MediaSourceService mediaSourceService;
    private final MediaSourceScanner mediaSourceScanner;
    private final MediaSourceWatcherService mediaSourceWatcherService;

    public MediaSourceInitializer(PlayqdProperties playqdProperties,
                                  MediaSourceService mediaSourceService,
                                  MediaSourceScanner mediaSourceScanner,
                                  MediaSourceWatcherService mediaSourceWatcherService) {
        this.playqdProperties = playqdProperties;
        this.mediaSourceService = mediaSourceService;
        this.mediaSourceScanner = mediaSourceScanner;
        this.mediaSourceWatcherService = mediaSourceWatcherService;
    }

    @Override
    public void run(ApplicationArguments args) {

        initMediaSources();

        mediaSourceService.getAll().forEach(mediaSource -> {
            if (mediaSource.autoScanOnStartUp()) {
                log.info("Re-scanning media source {} ...", mediaSource);
                mediaSourceScanner.scan(mediaSource.id());
            }
            if (mediaSource.watchable()) {
                mediaSourceWatcherService.watch(mediaSource);
            }
        });
    }

    private void initMediaSources() {
        var idGenerator = new AtomicLong(1);
        this.playqdProperties.getMediaSources().values()
            .forEach(config -> this.mediaSourceService.create(
                new MediaSource(
                    idGenerator.getAndIncrement(),
                    config.getName(),
                    Paths.get(config.getDir()),
                    config.isScanOnStart(),
                    config.isWatchable(),
                    config.getIgnoreDirs())));
    }

}
