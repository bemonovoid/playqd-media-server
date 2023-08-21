package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record AlbumArt(AlbumArtId id, String uri, AlbumArtMetadata metadata, BinaryData data) implements Serializable {

}
