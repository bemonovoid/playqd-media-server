package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record AlbumArt(String id, ImageResources resources, ImageMetadata metadata) implements Serializable {

}
