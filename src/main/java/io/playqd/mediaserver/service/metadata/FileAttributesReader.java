package io.playqd.mediaserver.service.metadata;

import java.nio.file.Path;
import java.util.Map;

public interface FileAttributesReader {

    Map<String, ?> read(Path path);
}
