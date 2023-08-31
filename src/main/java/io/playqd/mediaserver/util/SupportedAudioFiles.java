package io.playqd.mediaserver.util;

import org.jaudiotagger.audio.SupportedFileFormat;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class SupportedAudioFiles {

    public static final Set<String> SUPPORTED_AUDIO_EXTENSIONS = Arrays.stream(SupportedFileFormat.values())
            .map(SupportedFileFormat::getFilesuffix).collect(Collectors.toSet());

    public static boolean isSupportedAudioFile(File file) {
        return SUPPORTED_AUDIO_EXTENSIONS.contains(FileUtils.getFileExtension(file));
    }

    public static boolean isSupportedAudioFile(Path path) {
        return isSupportedAudioFile(FileUtils.getFileExtension(path.toString()));
    }

    public static boolean isSupportedAudioFile(String fileExtension) {
        return SUPPORTED_AUDIO_EXTENSIONS.contains(fileExtension);
    }

    private SupportedAudioFiles() {

    }
}
