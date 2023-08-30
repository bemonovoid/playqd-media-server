package io.playqd.mediaserver.model;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Set;

@Slf4j
public class SupportedImageFiles {

    private static final Set<String> ALBUM_ART_FILE_NAMES = Set.of("cover", "front", "folder", "albumart");

    public static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    public static boolean isAlbumArtFile(Path path, AudioFile audioFile) {
        if (isSupportedImageFile(path)) {
            var fileName = FileUtils.getFileNameWithoutExtension(path.getFileName().toString());
            var isAlbumArtFile = ALBUM_ART_FILE_NAMES.contains(fileName);
            if (!isAlbumArtFile) {
                isAlbumArtFile = fileName.toLowerCase().contains(audioFile.albumName().toLowerCase());
            }
            return isAlbumArtFile;
        }
        return false;
    }

    public static boolean isSupportedImageFile(Path path) {
        return isSupportedImageFile(FileUtils.getFileExtension(path.toString()));
    }

    public static boolean isSupportedImageFile(String fileExtension) {
        return SUPPORTED_IMAGE_EXTENSIONS.contains(fileExtension);
    }

}
