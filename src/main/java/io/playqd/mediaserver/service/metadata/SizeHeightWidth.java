package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record SizeHeightWidth(int height, int width) implements Serializable {

    public static SizeHeightWidth none() {
        return new SizeHeightWidth(0, 0);
    }
}
