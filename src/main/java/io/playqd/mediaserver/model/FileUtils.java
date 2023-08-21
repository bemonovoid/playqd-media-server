package io.playqd.mediaserver.model;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public abstract class FileUtils {

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileExtension(Path path) {
        return getFileExtension(path.toString());
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(".");
        return i == -1 ? "" : fileName.toLowerCase().substring(i + 1);
    }

    public static Tuple<String, String> getFileNameAndExtension(String fileName) {
        var extensionSeparatorIdx = fileName.lastIndexOf(".");
        var name = fileName.substring(0, extensionSeparatorIdx);
        var extension = fileName.substring(extensionSeparatorIdx + 1).toLowerCase();
        return Tuple.from(name, extension);
    }

    public static long getFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            log.error("Failed to get file size.", e);
            return 0;
        }
    }

    public static String getFileDisplaySize(File file) {
        if (file.isFile()) {
            return byteCountToDisplaySize(file.length());
        } else {
            return getDirDisplaySIze(file);
        }
    }

    public static String detectMimeType(Path path) {
        var mimeType = URLConnection.guessContentTypeFromName(path.toString());
        if (mimeType == null) {
            try {
                return Files.probeContentType(path);
            } catch (IOException e) {
                log.warn("Was unable to detect file's mime type with 'Files.probeContentType()'. {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    public static String getDirDisplaySIze(File file) {
        var dirSize = org.apache.commons.io.FileUtils.sizeOfDirectoryAsBigInteger(file);
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(dirSize);
    }

    public static String byteCountToDisplaySize(long size) {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(size);
    }

    public static LocalDateTime getLastModifiedDate(FileTime fileTime) {
        var zonedDateTime = fileTime.toInstant().atZone(ZoneOffset.UTC);
        return zonedDateTime.toLocalDateTime().withNano(0);
    }

}
