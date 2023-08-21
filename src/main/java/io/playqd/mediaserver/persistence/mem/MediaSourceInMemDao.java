package io.playqd.mediaserver.persistence.mem;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.service.mediasource.MediaSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component
class MediaSourceInMemDao implements MediaSourceDao {

    private final Map<Long, MediaSource> mediaSources;

    MediaSourceInMemDao(PlayqdProperties playqdProperties) {
        var idGenerator = new AtomicLong(1);
        this.mediaSources = playqdProperties.getMediaSources().stream()
                .collect(Collectors.toMap(
                        config -> idGenerator.get(),
                        config -> new MediaSource(
                                idGenerator.getAndIncrement(),
                                config.getName(),
                                Paths.get(config.getDir()),
                                config.isScanOnStart(),
                                config.getIgnoreDirs())));
    }

    @Override
    public List<MediaSource> getAll() {
        return new ArrayList<>(mediaSources.values());
    }

    @Override
    public MediaSource get(long id) {
        return mediaSources.get(id);
    }

}
