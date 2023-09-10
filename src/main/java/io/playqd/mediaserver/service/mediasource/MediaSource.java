package io.playqd.mediaserver.service.mediasource;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.nio.file.Path;
import java.util.Set;

public record MediaSource(long id,
                          String name,
                          Path path,
                          boolean autoScanOnStartUp,
                          boolean watchable,
                          @JsonInclude(JsonInclude.Include.NON_EMPTY)
                          Set<String> ignoredDirectories) {

  @Override
  public String toString() {
    return String.format("id=%s; name=%s; path = %s; ignored location(s): %s; watchable: %s; autoScanOnStartUp: %s;",
        id(), name(), path(), ignoredDirectories(), watchable(), autoScanOnStartUp());
  }

}