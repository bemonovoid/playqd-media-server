package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.util.FileUtils;
import io.playqd.mediaserver.util.SupportedAudioFiles;
import io.playqd.mediaserver.util.SupportedImageFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AbstractFolderContentFinder extends BrowsableObjectBuilder implements BrowsableObjectFinder {

    protected final BrowsableObjectDao browsableObjectDao;

    AbstractFolderContentFinder(BrowsableObjectDao browsableObjectDao) {
        this.browsableObjectDao = browsableObjectDao;
    }

    protected NestedObjectsCount countChildren(Path path) {
        try (Stream<Path> pathStream = Files.list(path)) {
            var mediaItems = pathStream.collect(Collectors.groupingBy(Files::isDirectory));
            var containerCount = (long) mediaItems.getOrDefault(true, Collections.emptyList()).size();
            var itemsCount = mediaItems.getOrDefault(false, Collections.emptyList()).stream()
                    .filter(AbstractFolderContentFinder::isSupportMediaItem)
                    .count();
            return new NestedObjectsCount(containerCount, itemsCount);
        } catch (IOException e) {
            throw new PlayqdException(String.format("Failed counting media source children at %s", path), e);
        }
    }

    private static boolean isSupportMediaItem(Path path) {
        var fileExtension = FileUtils.getFileExtension(path);
        return SupportedAudioFiles.isSupportedAudioFile(fileExtension) ||
                SupportedImageFiles.isSupportedImageFile(fileExtension);
    }

    protected record NestedObjectsCount(long childContainerCount, long childItemsCount) {

        long totalCount() {
            return childContainerCount() + childItemsCount();
        }
    }
}
