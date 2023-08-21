package io.playqd.mediaserver.service.mediasource;

import java.util.List;

public interface MediaSourceService {

    List<MediaSource> getAll();

    MediaSource get(long sourceId);

    MediaSourceContentInfo info(long sourceId);

}
