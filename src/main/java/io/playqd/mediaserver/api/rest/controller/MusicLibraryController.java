package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.model.Artist;
import io.playqd.mediaserver.persistence.AudioFileDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/musiclibrary")
class MusicLibraryController {

    private final AudioFileDao audioFileDao;

    MusicLibraryController(AudioFileDao audioFileDao) {
        this.audioFileDao = audioFileDao;
    }

    @GetMapping("/artists")
    Collection<Artist> artists() {
        return audioFileDao.getAllArtists();
    }
}
