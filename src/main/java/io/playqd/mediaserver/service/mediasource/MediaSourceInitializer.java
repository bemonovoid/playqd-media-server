package io.playqd.mediaserver.service.mediasource;

import io.playqd.mediaserver.model.StateVariables;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.StateVariableContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
        stateVariableContextHolder.getOrUpdate(StateVariables.SYSTEM_UPDATE_ID, () -> 1);
    }

}
