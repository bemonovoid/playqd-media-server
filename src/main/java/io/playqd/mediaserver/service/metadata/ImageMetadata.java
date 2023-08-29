package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record ImageMetadata(long size,
                            String mimeType,
                            SizeHeightWidth heightWidth) implements Serializable {
}
