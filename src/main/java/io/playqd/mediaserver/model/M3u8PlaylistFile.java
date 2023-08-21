package io.playqd.mediaserver.model;

import java.nio.file.Path;

public record M3u8PlaylistFile(String name, String format, Path location, long itemsCount) implements PlaylistFile {
}
