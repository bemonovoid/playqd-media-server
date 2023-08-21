package io.playqd.mediaserver.service.mediasource;

import java.nio.file.Path;

public interface MediaSourceScannerService {

    void scan(long sourceId);

    void scan(long sourceId, Path subPath);
}
