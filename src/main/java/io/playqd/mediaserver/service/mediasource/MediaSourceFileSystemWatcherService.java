package io.playqd.mediaserver.service.mediasource;

import com.sun.nio.file.ExtendedWatchEventModifier;
import io.playqd.mediaserver.model.event.MediaSourceContentChangedEvent;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
class MediaSourceFileSystemWatcherService implements MediaSourceWatcherService {

    private final ApplicationEventPublisher eventPublisher;

    private final Map<Long, WatchService> watchers = Collections.synchronizedMap(new HashMap<>());

    MediaSourceFileSystemWatcherService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Async
    @Override
    public void watch(MediaSource mediaSource) {
        var sourceId = mediaSource.id();
        if (watchers.containsKey(sourceId)) {
            log.warn("WatchService for media source with id: {} is already enabled.", sourceId);
            return; //TODO log warning
        }

        Path sourcePath = mediaSource.path();

        if (!Files.exists(sourcePath)) {
            log.error("Path '{}' in source with id: '{}' does not exist", sourcePath, sourceId);
            return;
        }

        log.info("Enabling new WatcherService for media source with id: {}", sourceId);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchers.put(sourceId, watchService);
            log.info("WatcherService for media source with id: {} was registered and is now starting ...", sourceId);
            startWatcher(watchService, mediaSource, sourcePath);
        } catch (IOException e) {
            log.error("Failed to register new WatcherService for media source with id: {}. {}",
                    sourceId, e.getMessage());
        }
    }

    @Async
    @Override
    public void stop(long sourceId) {
        WatchService watchService = watchers.remove(sourceId);
        if (watchService != null) {
            stopWatcher(sourceId, watchService);
        }
    }

    private void startWatcher(WatchService watchService, MediaSource mediaSource, Path watchable) {

        WatchEvent.Kind<?>[] events = {
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        };

        try {
            watchable.register(watchService, events, ExtendedWatchEventModifier.FILE_TREE);
//            watchable.register(watchService, events);
        } catch (IOException e) {
            log.error("Unexpected error occurred while starting watcher at: {}.", watchable, e);
            return;
        }

        boolean poll = true;

        log.info("Started WatcherService at: '{}'. Watching events: {}", watchable, Arrays.toString(events));

        final Map<String, Set<Path>> watchedContent = new HashMap<>();

        while (poll) {

            WatchKey key;

            try {
                if (watchedContent.isEmpty()) {
                    key = watchService.take();
                } else {
                    key = watchService.poll(5, TimeUnit.SECONDS);
                    if (key == null) {
                        notifyWatchedContentChanged(mediaSource, watchedContent);
                        continue;
                    }
                }

            } catch (ClosedWatchServiceException e) {
                log.warn("The WatcherService is being closed, may be application is now exiting. {}", e.getMessage());
                break;
            } catch (InterruptedException x) {
                log.error("Failed to establish watcher for mediaSourceId: {} in path: {}. Watcher will be closed",
                        mediaSource.id(), watchable);
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();

                log.info("Received audio source watcher event: {}", kind.name());

                @SuppressWarnings("unchecked")
                WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;

                Path createdPath = watchEvent.context();

                log.info("Watcher event context: {}", createdPath.toString());

                Path resolvedPath = watchable.resolve(createdPath);

                log.info("Full watcher event context: {}", resolvedPath);

                watchedContent.computeIfAbsent(kind.name(), value -> new HashSet<>()).add(resolvedPath);
            }

            poll = key.reset();

        }

        notifyWatchedContentChanged(mediaSource, watchedContent);

        stop(mediaSource.id());
    }

    private void notifyWatchedContentChanged(MediaSource mediaSource, Map<String, Set<Path>> watchedContent) {
        if (watchedContent.isEmpty()) {
            return;
        }
        Set<Path> changedContentDirs = watchedContent.values().stream()
                .flatMap(Collection::stream)
                .map(path -> {
                    var parentPath = path;
                    while (!parentPath.getParent().equals(mediaSource.path())) {
                        parentPath = parentPath.getParent();
                    }
                    return parentPath;
                })
                .collect(Collectors.toSet());

        eventPublisher.publishEvent(new MediaSourceContentChangedEvent(mediaSource, changedContentDirs));

        watchedContent.clear();
    }

    @PreDestroy
    private void stopAllBeforeExit() {
        log.info("Stopping {} WatcherService(s) before application termination ...", watchers.size());

        for (Map.Entry<Long, WatchService> watcherEntry : watchers.entrySet()) {
            var sourceId = watcherEntry.getKey();
            var watchService = watcherEntry.getValue();

            log.info("Stopping WatcherService for media source id: {}", sourceId);

            stopWatcher(sourceId, watchService);

            log.info("WatcherService for media source id: {} was successfully stopped", sourceId);
        }

        watchers.clear();

        log.info("All WatcherServices were unregistered");
    }

    private void stopWatcher(long sourceId, WatchService watchService) {
        try {
            WatchKey poll = watchService.poll();
            if (poll != null) {
                poll.cancel();
            }
            watchService.close();
        } catch (ClosedWatchServiceException e) {
            log.error("WatcherService for media source id: {} is already closed.", sourceId);
        } catch (IOException e) {
            log.error("Failed to gracefully stop WatcherService for media source id: {}. {}", sourceId, e.getMessage());
        }
    }

}
