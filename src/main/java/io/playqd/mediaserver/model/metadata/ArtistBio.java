package io.playqd.mediaserver.model.metadata;

import java.io.Serializable;

public record ArtistBio(String summary, String content) implements Serializable {
}
