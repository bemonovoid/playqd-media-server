package io.playqd.mediaserver.persistence;

import io.playqd.mediaserver.service.mediasource.MediaSource;

import java.util.List;

public interface MediaSourceDao {

    MediaSource get(long id);

    List<MediaSource> getAll();
}
