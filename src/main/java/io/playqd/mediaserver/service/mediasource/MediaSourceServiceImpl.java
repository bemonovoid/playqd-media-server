package io.playqd.mediaserver.service.mediasource;

import io.playqd.mediaserver.exception.CounterException;
import io.playqd.mediaserver.util.FileUtils;
import io.playqd.mediaserver.util.SupportedAudioFiles;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
class MediaSourceServiceImpl implements MediaSourceService {

    private final MediaSourceDao mediaSourceDao;

    public MediaSourceServiceImpl(MediaSourceDao mediaSourceDao) {
        this.mediaSourceDao = mediaSourceDao;
    }

    @Override
    public MediaSource get(long sourceId) {
        return mediaSourceDao.get(sourceId);
    }

    @Override
    public List<MediaSource> getAll() {
        return mediaSourceDao.getAll();
    }

    @Override
    public MediaSourceContentInfo info(long sourceId) {
        var mediaSource = get(sourceId);
        try (Stream<Path> filesStream = Files.walk(mediaSource.path())) {
            var extensionCounts = filesStream
                    .filter(SupportedAudioFiles::isSupportedAudioFile)
                    .collect(Collectors.groupingBy(FileUtils::getFileExtension, Collectors.counting()))
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
            return new MediaSourceContentInfo(mediaSource, totalCount, extensionCounts);
        } catch (IOException e) {
            throw new CounterException(e);
        }
    }

}
