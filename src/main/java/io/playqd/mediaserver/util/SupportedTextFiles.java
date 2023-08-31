package io.playqd.mediaserver.util;

import java.nio.file.Path;
import java.util.Set;

public abstract class SupportedTextFiles {

    public static final Set<String> SUPPORTED_EXTENSIONS = Set.of("log", "txt");

    public static boolean isSupportedTextFile(Path path) {
        return isSupportedTextFile(FileUtils.getFileExtension(path.toString()));
    }

    public static boolean isSupportedTextFile(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension);
    }

}
