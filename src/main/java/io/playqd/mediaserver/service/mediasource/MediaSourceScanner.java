package io.playqd.mediaserver.service.mediasource;

import java.nio.file.Path;

public interface MediaSourceScanner {

    void scan(long sourceId);

    void scan(long sourceId, Path subPath);
}
