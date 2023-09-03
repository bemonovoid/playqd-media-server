package io.playqd.mediaserver.config.lifecycle;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@Order(PlayqdApplicationRunnerOrder.PLAYLISTS_DIRECTORY_INITIALIZER)
class PlaylistsDirectoryInitializer implements ApplicationRunner {

    private final PlayqdProperties playqdProperties;

    PlaylistsDirectoryInitializer(PlayqdProperties playqdProperties) {
        this.playqdProperties = playqdProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var playlistsDir = getOrCreatePlaylistsDir();
    }

    private Path getOrCreatePlaylistsDir() {
        var workingDir = playqdProperties.getWorkingDirOrDefault();
        try {
            if (!Files.isDirectory(workingDir)) {
                throw new IllegalStateException(String.format("Working dir is not a directory. %s", workingDir));
            }
            var playlistsDir = playqdProperties.getPlaylistsDir();
            if (!Files.exists(playlistsDir)) {
                log.info("Creating playlists directory in working dir ...");
                Files.createDirectory(playlistsDir);
                log.info("Playlists directory was successfully created: {}", playlistsDir);
            }
            log.info("Playlists directory already exists.");
            return playlistsDir;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
