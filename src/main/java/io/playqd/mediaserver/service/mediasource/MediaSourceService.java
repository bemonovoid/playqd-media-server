package io.playqd.mediaserver.service.mediasource;

import java.util.List;

public interface MediaSourceService {

    List<MediaSource> getAll();

    MediaSource get(long sourceId);

    MediaSource create(MediaSource mediaSource);

    MediaSourceContentInfo info(long sourceId);

}
