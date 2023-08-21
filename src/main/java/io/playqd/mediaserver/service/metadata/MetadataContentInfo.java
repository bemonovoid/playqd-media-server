package io.playqd.mediaserver.service.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

public record MetadataContentInfo(long totalCount,
                                  Map<String, Long> formats,
                                  boolean inSyncWithSource,
                                  @JsonInclude(JsonInclude.Include.NON_EMPTY) List<String> details) {
}
