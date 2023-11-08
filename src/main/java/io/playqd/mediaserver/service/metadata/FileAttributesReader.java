package io.playqd.mediaserver.service.metadata;

import java.nio.file.Path;
import java.util.Map;

public interface FileAttributesReader {

    Map<String, Object> read(Path path);
}
