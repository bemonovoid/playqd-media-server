package io.playqd.mediaserver.service.metadata;

import jakarta.persistence.Transient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public interface AudioFileAttributes {

    long getId();

    String getLocation();

    String getExtension();

    Instant getFileLastModifiedDate();

    @Transient
    default Path getPath() {
        return Paths.get(getLocation());
    }

    static boolean wasModified(AudioFileAttributes prevScannedAudioFile) {
        var newLastModifiedDate = Instant.ofEpochMilli(prevScannedAudioFile.getPath().toFile().lastModified());
        return prevScannedAudioFile.getFileLastModifiedDate().isBefore(newLastModifiedDate);
    }
}
