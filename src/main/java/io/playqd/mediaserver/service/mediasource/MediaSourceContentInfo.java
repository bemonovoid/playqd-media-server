package io.playqd.mediaserver.service.mediasource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public record MediaSourceContentInfo(@JsonIgnore MediaSource mediaSource, long totalCount, Map<String, Long> formats) {
}
