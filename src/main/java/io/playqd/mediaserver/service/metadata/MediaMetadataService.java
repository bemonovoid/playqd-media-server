package io.playqd.mediaserver.service.metadata;


import io.playqd.mediaserver.model.AudioFile;

public interface MediaMetadataService {

    MetadataContentInfo getInfo(long sourceId);

    long clear(long sourceId);
}
