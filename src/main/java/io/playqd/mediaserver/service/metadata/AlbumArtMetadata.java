package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record AlbumArtMetadata(long fileSize,
                               String mimeType,
                               SizeHeightWidth heightWidth) implements Serializable {
}
