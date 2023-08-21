package io.playqd.mediaserver.model;

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
        return SUPPORTED_AUDIO_EXTENSIONS.contains(FileUtils.getFileExtension(path.toString()));
    }

    private SupportedAudioFiles() {

    }
}
