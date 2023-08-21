package io.playqd.mediaserver.service.jtagger;

import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public final class AudioFileTagReader {

    private static final List<FieldKey> ARTIST_NAME_TAGS =
            List.of(FieldKey.ARTIST, FieldKey.ALBUM_ARTIST, FieldKey.ORIGINAL_ARTIST, FieldKey.COMPOSER);

    static List<FieldKey> getArtistNameTagsOrdered() {
        return ARTIST_NAME_TAGS;
    }

    public static String readFromTag(AudioFile audioFile, FieldKey key) {
        return readFromTag(audioFile, key, () -> null);
    }

    public static String readFromTag(AudioFile audioFile, FieldKey key, Supplier<String> defaultValue) {
        try  {
            return Optional.ofNullable(audioFile.getTag())
                    .map(tag -> tag.getFirst(key))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .orElseGet(defaultValue);
        } catch (Exception e) { // UnsupportedOperationException | KeyNotFoundException
            log.error("Failed to read tag: '{}' from:  {}. Error details: {}",
                    key.name(), audioFile.getFile().getAbsolutePath(), e.getMessage());
            return null;
        }
    }

    private AudioFileTagReader() {

    }
}
