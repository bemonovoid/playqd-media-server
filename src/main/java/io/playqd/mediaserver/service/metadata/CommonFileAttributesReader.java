package io.playqd.mediaserver.service.metadata;

import io.playqd.mediaserver.model.FileUtils;
import io.playqd.mediaserver.exception.AudioMetadataReadException;
import io.playqd.mediaserver.persistence.jpa.entity.AudioFileJpaEntity;
import io.playqd.mediaserver.persistence.jpa.entity.AuditableEntity;
import io.playqd.mediaserver.util.MimeTypeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CommonFileAttributesReader implements FileAttributesReader {

    @Override
    public Map<String, ?> read(Path path) {
        try {
            var file = path.toFile();
            var fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            var fileNameAndExtension = FileUtils.getFileNameAndExtension(file.getName());

            var params = new LinkedHashMap<String, Object>();

            // File attributes
            params.put(AudioFileJpaEntity.COL_SIZE, fileAttributes.size());
            params.put(AudioFileJpaEntity.COL_NAME, fileNameAndExtension.left());
            params.put(AudioFileJpaEntity.COL_LOCATION, path.toString());
            params.put(AudioFileJpaEntity.COL_EXTENSION, fileNameAndExtension.right());
            params.put(AudioFileJpaEntity.COL_MIME_TYPE, MimeTypeUtil.detect(path));
            params.put(AudioFileJpaEntity.COL_FILE_LAST_SCANNED_DATE, Instant.now());
            params.put(AudioFileJpaEntity.COL_FILE_LAST_MODIFIED_DATE, Instant.ofEpochMilli(file.lastModified()));

            // Audit
            params.put(AuditableEntity.COL_CREATED_BY, "system");
            params.put(AuditableEntity.COL_CREATED_DATE, Instant.now());

            return params;
        } catch (IOException e) {
            throw new AudioMetadataReadException(e);
        }

    }
}
