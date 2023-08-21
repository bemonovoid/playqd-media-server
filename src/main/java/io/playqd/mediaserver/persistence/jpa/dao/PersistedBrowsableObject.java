package io.playqd.mediaserver.persistence.jpa.dao;

import java.nio.file.Path;
import java.util.function.Supplier;

public record PersistedBrowsableObject(long id,
                                       Long parentId,
                                       String objectId,
                                       String dcTitle,
                                       Path location,
                                       Supplier<Long> childrenCount) {
}
