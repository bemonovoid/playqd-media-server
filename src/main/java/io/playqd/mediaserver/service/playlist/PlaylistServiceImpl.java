package io.playqd.mediaserver.service.playlist;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.model.*;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.upnp.server.UUIDV3Ids;
import io.playqd.mediaserver.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class PlaylistServiceImpl implements PlaylistService {

    private static final Set<String> SUPPORTED_FORMATS = Set.of("m3u8");

    private final Map<String, PlaylistFilePath> playlistFileRefs = new HashMap<>();

    private final Path playlistsDir;
    private final AudioFileDao audioFileDao;

    public PlaylistServiceImpl(PlayqdProperties playqdProperties, AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
        this.playlistsDir = playqdProperties.getPlaylistsDir();
    }

    public PlaylistFile getPlaylistFile(String playlistId) {
        var playlistFileRef = playlistFileRefs.computeIfAbsent(playlistId, key -> findPlaylistFileRef(key).orElse(null));
        if (playlistFileRef == null) {
            throw new PlayqdException("Not found");
        }
        var playlistLocation = playlistFileRef.location();
        var nameAndFormat = FileUtils.getFileNameAndExtension(playlistLocation.toString());
        return new M3u8PlaylistFile(
                nameAndFormat.left(), nameAndFormat.right(), playlistLocation, countPlaylistItems(playlistLocation));
    }

    @Override
    public List<AudioFile> getPlaylistAudioFiles(String playlistId) {
        var playlistFile = getPlaylistFile(playlistId);
        try (Stream<String> lines = Files.lines(playlistFile.location())) {
            var fileLocations = lines
                    .filter(line -> !line.startsWith("#"))
                    .map(PlaylistServiceImpl::getValidPath)
                    .filter(Objects::nonNull)
                    .map(Path::toString)
                    .toList();
            return audioFileDao.getAudioFilesByLocationIn(fileLocations);
        } catch (IOException e) {
            log.error("", e);
            return Collections.emptyList();
        }
    }

    private Optional<PlaylistFilePath> findPlaylistFileRef(String playlistId) {
        try (Stream<Path> files = Files.list(playlistsDir)) {
            return files
                    .filter(path -> SUPPORTED_FORMATS.contains(FileUtils.getFileExtension(path)))
                    .filter(path -> UUIDV3Ids.create(path.toString()).equals(playlistId))
                    .map(PlaylistFilePath::new)
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
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
                    .filter(line -> getValidPath(line) != null)
                    .count();
        } catch (IOException e) {
            log.error("Count failed.", e);
            return 0;
        }
    }

    private static Path getValidPath(String line) {
        try {
            return Paths.get(line);
        } catch (Exception e) {
            log.warn("Path wasn't a valid file.", e);
            return null;
        }
    }

    private record PlaylistFilePath(Path location) {

    }
}
