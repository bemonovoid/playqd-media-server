package io.playqd.mediaserver.config.lifecycle;

import io.playqd.mediaserver.service.upnp.server.service.StateVariableName;
import io.playqd.mediaserver.service.mediasource.MediaSourceScannerService;
import io.playqd.mediaserver.service.mediasource.MediaSourceService;
import io.playqd.mediaserver.service.mediasource.MediaSourceWatcherService;
import io.playqd.mediaserver.service.upnp.server.service.StateVariableContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(PlayqdApplicationRunnerOrder.MEDIA_SOURCE_INITIALIZER)
class MediaSourceInitializer implements ApplicationRunner {

    private final MediaSourceService mediaSourceService;
    private final MediaSourceScannerService mediaSourceScannerService;
    private final MediaSourceWatcherService mediaSourceWatcherService;
    private final StateVariableContextHolder stateVariableContextHolder;

    MediaSourceInitializer(MediaSourceService mediaSourceService,
                           MediaSourceScannerService mediaSourceScannerService,
                           MediaSourceWatcherService mediaSourceWatcherService,
                           StateVariableContextHolder stateVariableContextHolder) {
        this.mediaSourceService = mediaSourceService;
        this.mediaSourceScannerService = mediaSourceScannerService;
        this.mediaSourceWatcherService = mediaSourceWatcherService;
        this.stateVariableContextHolder = stateVariableContextHolder;
    }

    @Override
    public void run(ApplicationArguments args) {

        initSystemUpdateId();

        mediaSourceService.getAll().forEach(mediaSource -> {
            if (mediaSource.autoScanOnStartUp()) {
                log.info("Re-scanning media source {} ...", mediaSource.description());
                mediaSourceScannerService.scan(mediaSource.id());
            }
            mediaSourceWatcherService.watch(mediaSource);
        });
    }

    private void initSystemUpdateId() {
        stateVariableContextHolder.getOrUpdate(StateVariableName.SYSTEM_UPDATE_ID, () -> 1);
    }

}
