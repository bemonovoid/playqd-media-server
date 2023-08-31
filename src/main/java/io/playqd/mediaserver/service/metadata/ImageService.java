package io.playqd.mediaserver.service.metadata;

import io.playqd.mediaserver.model.AudioFile;

import java.util.Optional;

public interface ImageService {

    Optional<AlbumArt> get(String albumId);

    Optional<AlbumArt> get(AudioFile audioFile);

    Optional<AlbumArt> get(String albumId, String albumFolderImageFileName);

    Optional<byte[]> getFromBrowsableObject(String objectId);
}
