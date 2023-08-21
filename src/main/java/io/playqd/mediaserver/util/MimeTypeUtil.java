package io.playqd.mediaserver.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public final class MimeTypeUtil {

    private final static Tika TIKA_INSTANCE = new Tika();

    public static String detect(Path path) {
        try {
            return TIKA_INSTANCE.detect(path);
        } catch (IOException e) {
            log.error("Failed to detect mime type for {}. ", path, e);
            return "";
        }
    }

    public static String detect(byte[] data) {
        return TIKA_INSTANCE.detect(data);
    }

    public static String detect(String fileLocation) {
        return TIKA_INSTANCE.detect(fileLocation);
    }

    private MimeTypeUtil () {

    }
}
