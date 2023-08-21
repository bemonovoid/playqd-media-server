package io.playqd.mediaserver.model.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ImageSize(SizeName name, @JsonProperty("size_info") SizeInfo sizeInfo) implements Serializable {
}
