package io.playqd.mediaserver.persistence.simple;

import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.service.mediasource.MediaSource;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MediaSourceDaoImpl implements MediaSourceDao {

    private final Map<Long, MediaSource> mediaSources = new ConcurrentHashMap<>();

    @Override
    public List<MediaSource> getAll() {
        return new ArrayList<>(mediaSources.values());
    }

    @Override
    public MediaSource get(long id) {
        return mediaSources.get(id);
    }

    @Override
    public MediaSource create(MediaSource mediaSource) {
        if (mediaSources.containsKey(mediaSource.id())) {
            throw new PlayqdException(String.format("Media source already exists. %s", mediaSource));
        }
        return mediaSources.put(mediaSource.id(), mediaSource);
    }
}
