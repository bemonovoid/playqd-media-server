package io.playqd.mediaserver.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public interface AudioFile {

    Long id();

    String artistId();

    String albumId();

    String trackId();

    String genreId();

    long size();

    String name();

    String location();

    default Path path() {
        return Paths.get(location());
    }

    String extension();

    String mimeType();

    int playbackCount();

    Instant fileLastPlaybackDate();

    Instant fileLastScannedDate();

    Instant fileLastModifiedDate();

    String format();

    String bitRate();

    String channels();

    boolean lossless();

    String sampleRate();

    String encodingType();

    int bitsPerSample();

    String artistName();

    String artistCountry();

    String albumName();

    String albumReleaseDate();

    String trackName();

    String trackNumber();

    int trackLength();

    double preciseTrackLength();

    String comment();

    String lyrics();

    String genre();

    boolean artworkEmbedded();

    String mbTrackId();

    String mbArtistId();

    String mbReleaseType();

    String mbReleaseGroupId();

    String getAudioStreamUri();

    static Optional<String> getLastPlaybackTimeFormatted(AudioFile audioFile) {
        return Optional.ofNullable(audioFile.fileLastPlaybackDate())
                .map(instant -> LocalDateTime.ofInstant(
                        instant, ZoneId.systemDefault()).toString().replaceFirst("T", "'T'"));
    }
}
