package io.playqd.mediaserver.service.mediasource;

import io.playqd.mediaserver.exception.MediaSourceScannerException;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.service.metadata.AudioFileAttributes;
import io.playqd.mediaserver.service.metadata.FileAttributesReader;
import io.playqd.mediaserver.util.SupportedAudioFiles;
import io.playqd.mediaserver.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MediaSourceScannerImpl implements MediaSourceScanner {

    private final AudioFileDao audioFileDao;
    private final MediaSourceDao mediaSourceDao;
    private final FileAttributesReader fileMetadataReader;

    public MediaSourceScannerImpl(AudioFileDao audioFileDao,
                                  MediaSourceDao mediaSourceDao,
                                  FileAttributesReader fileMetadataReader) {
        this.audioFileDao = audioFileDao;
        this.mediaSourceDao = mediaSourceDao;
        this.fileMetadataReader = fileMetadataReader;
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scan(long sourceId) {
        scan(sourceId, null);
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public void scan(long sourceId, Path subPath) {
        var mediaSource = mediaSourceDao.get(sourceId);

        var path = subPath != null ? subPath : mediaSource.path();

        if (!Files.exists(path)) {
            log.warn("Scan for media source with id: {} was aborted. {} location does not exist", sourceId, path);
            return;
        }

        try (Stream<Path> pathsStream = Files.walk(path)) {

            var scanStartTime = LocalDateTime.now();

            var prevScannedAudioFiles = Collections.synchronizedMap(
                    audioFileDao.streamByLocationStartsWith(path, AudioFileAttributes.class)
                            .filter(a -> a.getPath().startsWith(path))
                            .collect(Collectors.toMap(AudioFileAttributes::getPath, value -> value)));

            var newAudioFiles = Collections.synchronizedList(new LinkedList<Map<String, ?>>());
            var modifiedAudioFiles = Collections.synchronizedMap(new LinkedHashMap<Long, Map<String, ?>>());

            var totalFilesScanned = new AtomicLong(0);

            pathsStream
                    .parallel()
                    .filter(ignoredDirs(mediaSource.ignoredDirectories()))
                    .filter(SupportedAudioFiles::isSupportedAudioFile)
                    .forEach(p -> {
                        totalFilesScanned.incrementAndGet();
                        var prevScannedAudioFile = prevScannedAudioFiles.remove(p);
                        if (prevScannedAudioFile != null) {
                            if (AudioFileAttributes.wasModified(prevScannedAudioFile)) {
                                modifiedAudioFiles.put(prevScannedAudioFile.getId(), fileMetadataReader.read(p));
                            }
                            // Do nothing. Files wasn't modified and had already been scanned.
                        } else {
                            newAudioFiles.add(fileMetadataReader.read(p));
                        }
                    });

            var obsoleteAudioFiles = prevScannedAudioFiles.values().stream().map(AudioFileAttributes::getId).toList();

            deleteObsoleteMetadata(obsoleteAudioFiles);
            addNewMetadata(newAudioFiles);
            updateModifiedMetadata(modifiedAudioFiles);

            var scanDuration = Duration.between(scanStartTime.toLocalTime(), LocalTime.now());
            var duration = TimeUtils.durationToDisplayString(scanDuration);

            log.info(getReportStringTemplate(),
                mediaSource,
                newAudioFiles.size(),
                modifiedAudioFiles.size(),
                obsoleteAudioFiles.size(),
                duration,
                totalFilesScanned.get());

        } catch (IOException e) {
            throw new MediaSourceScannerException(e);
        }
    }

    private void deleteObsoleteMetadata(List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        var rowsDeleted = audioFileDao.deleteAllByIds(ids);
        log.info("Successfully deleted {} missing items", rowsDeleted);
    }

    private void addNewMetadata(List<Map<String, ?>> newItemsInsertParams) {
        if (!newItemsInsertParams.isEmpty()) {
            audioFileDao.insertAll(newItemsInsertParams);
            audioFileDao.setNewLastRecentlyAddedDate(LocalDateTime.now());
        }
    }

    private void updateModifiedMetadata(Map<Long, Map<String, ?>> modifiedItemsInsertParams) {
        if (!modifiedItemsInsertParams.isEmpty()) {
            audioFileDao.updateAll(modifiedItemsInsertParams);
        }
    }

    private static Predicate<Path> ignoredDirs(Set<String> ignoredDirs) {
        if (CollectionUtils.isEmpty(ignoredDirs)) {
            return path -> true;
        }
        return path -> !ignoredDirs.contains(path.getParent().getFileName().toString());
    }

    private static String getReportStringTemplate() {
        return """
            
            
            <-------------SCAN REPORT START------------->
                      
            {}
                                   
            ------------
            files added:    {}
            files modified: {}
            files deleted:  {}
            ------------
            Duration:       {}
            Files scanned:  {}
                        
            <-------------SCAN REPORT END--------------->
            """;
    }

}
