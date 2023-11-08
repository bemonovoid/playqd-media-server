package io.playqd.mediaserver.persistence;

import io.playqd.mediaserver.model.Album;
import io.playqd.mediaserver.model.Artist;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface AudioFileDao {

    Page<Artist> getArtists(Pageable pageable);

    Page<AudioFile> getAudioFiles(Pageable pageable);

    long countGenres();

    long countArtists();

    long countPlayed();

    long countRecentlyAdded();

    long countAlbumAudioFiles(String albumId);

    long countArtistAudioFiles(String artistId);

    AudioFile getAudioFile(long id);

    AudioFile getFirstAudioFileByAlbumId(String albumId);

    List<Genre> getAllGenres();

    Page<AudioFile> getRecentlyAdded(Pageable pageable);

    Page<AudioFile> getPlayed(Pageable pageable);

    List<Artist> getGenreArtists(String genreId);

    List<Album> getGenreAlbums(String genreId);

    List<Album> getArtistAlbums(String artistId);

    List<AudioFile> getAudioFilesByAlbumId(String albumId);

    List<AudioFile> getAudioFilesByArtistId(String artistId);

    List<AudioFile> getAudioFilesByLocationIn(Collection<String> locations);

    <T> Stream<T> streamByLocationStartsWith(Path basePath, Class<T> type);

    void updateAudioFileLastPlaybackDate(long audioFileId);

    void setNewLastRecentlyAddedDate(LocalDateTime lastRecentlyAddedDateTime);

    int insertAll(List<Map<String, Object>> audioFilesData);

    int updateAll(Map<Long, Map<String, Object>> audioFilesData);

    long deleteAllByIds(List<Long> ids);

    long deleteAllByLocationsStartsWith(Path path);
}
