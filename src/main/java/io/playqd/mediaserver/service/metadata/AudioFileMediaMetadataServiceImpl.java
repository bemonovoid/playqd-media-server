package io.playqd.mediaserver.service.metadata;

import io.playqd.mediaserver.exception.CounterException;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.mediasource.MediaSourceContentInfo;
import io.playqd.mediaserver.service.mediasource.MediaSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
class AudioFileMediaMetadataServiceImpl implements MediaMetadataService {

    private final AudioFileDao audioFileDao;
    private final MediaSourceService mediaSourceService;

    AudioFileMediaMetadataServiceImpl(AudioFileDao audioFileDao,
                                      MediaSourceService mediaSourceService) {
        this.audioFileDao = audioFileDao;
        this.mediaSourceService = mediaSourceService;
    }

    @Override
    public MetadataContentInfo getInfo(long sourceId) throws CounterException {
        var mediaSourceContentInfo = mediaSourceService.info(sourceId);

        try (Stream<AudioFileAttributes> audioFileStream = audioFileDao.streamByLocationStartsWith(
                mediaSourceContentInfo.mediaSource().path(),
                AudioFileAttributes.class)) {

            var extensionCounts = audioFileStream
                    .collect(Collectors.groupingBy(AudioFileAttributes::getExtension, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
            var totalCount = extensionCounts.values().stream()
                    .collect(Collectors.summarizingLong(value -> value))
                    .getSum();

            var diffDetails = new ArrayList<String>();

            var isInSyncWithSource =
                    isInSyncWithSource(mediaSourceContentInfo, totalCount, extensionCounts, diffDetails);

            return new MetadataContentInfo(totalCount, extensionCounts, isInSyncWithSource, diffDetails);

        } catch (Exception e) {
            throw new CounterException(e);
        }
    }

    @Override
    public long clear(long sourceId) {
        return audioFileDao.deleteAllByLocationsStartsWith(mediaSourceService.get(sourceId).path());
    }

    private boolean isInSyncWithSource(MediaSourceContentInfo mediaSourceContentInfo,
                                       long metadataTotalCount,
                                       Map<String, Long> metadataFormats,
                                       List<String> detailsHolder) {

        var mediaSource = mediaSourceContentInfo.mediaSource();

        if (metadataTotalCount < mediaSourceContentInfo.totalCount()) {
            detailsHolder.add(String.format("Scanned metadata store is out of sync and is %s file(s) behind.",
                    mediaSourceContentInfo.totalCount() - metadataTotalCount));
        } else if (metadataTotalCount > mediaSourceContentInfo.totalCount()) {
            detailsHolder.add(String.format("Scanned metadata store is out of sync and has obsolete %s file(s).",
                    metadataTotalCount - mediaSourceContentInfo.totalCount()));
        }

        var modifiedFiles = audioFileDao.streamByLocationStartsWith(mediaSource.path(), AudioFileAttributes.class)
                .parallel()
                .filter(AudioFileAttributes::wasModified)
                .toList();;

        if (modifiedFiles.isEmpty()) {
            return metadataTotalCount == mediaSourceContentInfo.totalCount()
                    && metadataFormats.equals(mediaSourceContentInfo.formats());
        } else {
            var msgTemplate = "Source file was modified. Re-sync saved metadata item with id: %s, at path: %s";
            modifiedFiles.stream()
                    .map(m -> String.format(msgTemplate, m.getId(), m.getLocation()))
                    .forEach(detailsHolder::add);
            return false;
        }
    }
}
