package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record AlbumArt(AlbumArtId id, ImageResources resources, ImageMetadata metadata) implements Serializable {

}
