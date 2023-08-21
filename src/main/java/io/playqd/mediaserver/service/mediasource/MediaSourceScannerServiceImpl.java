package io.playqd.mediaserver.service.mediasource;

import io.playqd.mediaserver.exception.MediaSourceScannerException;
import io.playqd.mediaserver.model.SupportedAudioFiles;
import io.playqd.mediaserver.model.TimeUtils;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.service.metadata.AudioFileAttributes;
import io.playqd.mediaserver.service.metadata.FileAttributesReader;
import io.playqd.mediaserver.templates.TemplateNames;
import io.playqd.mediaserver.templates.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MediaSourceScannerServiceImpl implements MediaSourceScannerService {

    private final AudioFileDao audioFileDao;
    private final MediaSourceDao mediaSourceDao;
    private final TemplateService templateService;
    private final FileAttributesReader fileMetadataReader;

    public MediaSourceScannerServiceImpl(AudioFileDao audioFileDao,
                                         MediaSourceDao mediaSourceDao,
                                         TemplateService templateService,
                                         FileAttributesReader fileMetadataReader) {
        this.audioFileDao = audioFileDao;
        this.mediaSourceDao = mediaSourceDao;
        this.templateService = templateService;
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

            pathsStream
                    .parallel()
                    .filter(ignoredDirs(mediaSource.ignoredDirectories()))
                    .filter(SupportedAudioFiles::isSupportedAudioFile)
                    .forEach(p -> {
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

            Map<String, Object> counts = Map.of(
                    "addedFilesCount", newAudioFiles.size(),
                    "modifiedFilesCount", modifiedAudioFiles.size(),
                    "deletedFilesCount", obsoleteAudioFiles.size());

            Map<String, Object> data = Map.of(
                    "mediaSource", mediaSource,
                    "counts", counts,
                    "duration", TimeUtils.durationToDisplayString(scanDuration));

            log.info(templateService.processToString(TemplateNames.SCAN_REPORT, data));

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

}
