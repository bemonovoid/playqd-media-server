package io.playqd.mediaserver.service.metadata;

public interface MediaMetadataService {

    MetadataContentInfo getInfo(long sourceId);

    long clear(long sourceId);
}
