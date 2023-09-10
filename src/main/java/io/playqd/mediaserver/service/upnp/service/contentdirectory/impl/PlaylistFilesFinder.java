package io.playqd.mediaserver.service.upnp.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.model.M3u8PlaylistFile;
import io.playqd.mediaserver.model.PlaylistFile;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.upnp.server.UUIDV3Ids;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowseContext;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.DcTagValues;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.ObjectIdPattern;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpTagValues;
import io.playqd.mediaserver.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class PlaylistFilesFinder implements BrowsableObjectFinder {

    private static final Set<String> SUPPORTED_FORMATS = Set.of("m3u8");

    private final PlayqdProperties playqdProperties;

    public PlaylistFilesFinder(PlayqdProperties playqdProperties) {
        this.playqdProperties = playqdProperties;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        try (Stream<Path> playlistFiles = Files.list(playqdProperties.getPlaylistsDir())) {
            var result = playlistFiles
                    .filter(path -> SUPPORTED_FORMATS.contains(FileUtils.getFileExtension(path)))
                    .map(PlaylistFilesFinder::createPlaylistFromFile)
                    .map(playlistFile -> buildContainerObject(context, playlistFile))
                    .toList();
            return new BrowseResult(result.size(), result.size(), result);
        } catch (IOException e) {
            log.error("Playlists lookup failed.", e);
            return BrowseResult.empty();
        }
    }

    private static PlaylistFile createPlaylistFromFile(Path path) {
        var fileNameExtension = FileUtils.getFileNameAndExtension(path.getFileName().toString());
        return new M3u8PlaylistFile(fileNameExtension.left(), fileNameExtension.left(), path, countPlaylistItems(path));
    }

    private static long countPlaylistItems(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .filter(line -> !line.startsWith("#"))
                    .filter(PlaylistFilesFinder::isValidFilePath)
                    .count();
        } catch (IOException e) {
            log.error("Count failed.", e);
            return 0;
        }
    }

    private static boolean isValidFilePath(String line) {
        try {
            Paths.get(line);
            return true;
        } catch (Exception e) {
            log.warn("Path wasn't a valid file.", e);
            return false;
        }
    }

    private static BrowsableObject buildContainerObject(BrowseContext context, PlaylistFile playlistFile) {
        var objectId = ObjectIdPattern.PLAYLIST_PATH.compile(UUIDV3Ids.create(playlistFile.location().toString()));
        return BrowsableObjectImpl.builder()
                .objectId(objectId)
                .parentObjectId(context.getObjectId())
                .searchable(true)
                .childCount(playlistFile.itemsCount())
                .dc(DcTagValues.builder().title(playlistFile.name()).build())
                .upnp(buildUpnpTagValues(playlistFile))
                .build();
    }

    private static UpnpTagValues buildUpnpTagValues(PlaylistFile playlistFile) {
        return UpnpTagValues.builder().upnpClass(UpnpClass.playlistContainer).build();
    }
}
