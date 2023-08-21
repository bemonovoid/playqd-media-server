package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

@FunctionalInterface
public interface AlbumArtId extends Serializable {

    String get();

    record AlbumId(String albumId) implements AlbumArtId {

        @Override
        public String get() {
            return albumId;
        }
    }

    record AlbumFolderImageFileName(String albumId, String fileName) implements AlbumArtId {

        @Override
        public String get() {
            return albumId + "/" + fileName;
        }
    }

}
