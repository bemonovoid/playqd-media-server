package io.playqd.mediaserver.service.metadata;

import java.io.Serializable;

public record ImageResource(String uri, byte[] byteArray) implements Serializable {
}
