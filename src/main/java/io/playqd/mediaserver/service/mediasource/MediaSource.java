package io.playqd.mediaserver.service.mediasource;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.nio.file.Path;
import java.util.Set;

public record MediaSource(long id,
                          String name,
                          Path path,
                          boolean autoScanOnStartUp,
                          @JsonInclude(JsonInclude.Include.NON_EMPTY)
                          Set<String> ignoredDirectories) {

    public String description() {
        return String.format("id=%s;name=%s;path=%s", id(), name(), path());
    }
}