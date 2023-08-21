package io.playqd.mediaserver.model.metadata;

import java.io.Serializable;

public record Image(String url, ImageSize size) implements Serializable {

    public static Image fromUrl(String url) {
        return new Image(url, null);
    }
}
