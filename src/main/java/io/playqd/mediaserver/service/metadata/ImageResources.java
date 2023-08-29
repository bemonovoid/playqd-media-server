package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;
import java.util.Map;

public record ImageResources(ImageResource original,
                             Map<ImageSizeRequestParam, ImageResource> resized) implements Serializable {

    public ImageResource getResizedOrOriginal(ImageSizeRequestParam imageSizeRequestParam) {
        if (imageSizeRequestParam == null) {
            return original;
        }
        return resized.getOrDefault(imageSizeRequestParam, original);
    }
}
