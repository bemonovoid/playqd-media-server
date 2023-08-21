package io.playqd.mediaserver.model.event;

import io.playqd.mediaserver.service.mediasource.MediaSource;

import java.nio.file.Path;
import java.util.Set;

public record MediaSourceContentChangedEvent(MediaSource mediaSource, Set<Path> changedContentDirs) {
}
