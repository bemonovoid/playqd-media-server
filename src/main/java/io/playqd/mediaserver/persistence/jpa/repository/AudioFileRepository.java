package io.playqd.mediaserver.persistence.jpa.repository;

import io.playqd.mediaserver.persistence.jpa.dao.AlbumsProjection;
import io.playqd.mediaserver.persistence.jpa.dao.ArtistProjection;
import io.playqd.mediaserver.persistence.jpa.dao.GenreProjection;
import io.playqd.mediaserver.persistence.jpa.entity.AudioFileJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface AudioFileRepository extends IdentityJpaRepository<AudioFileJpaEntity> {

    AudioFileJpaEntity findFirstByAlbumId(String albumId);

    <T> Stream<T> findByLocationIsStartingWith(String basePath, Class<T> type);

    List<AudioFileJpaEntity> findAllByLocationIn(Collection<String> locations);

    List<AudioFileJpaEntity> findAllByArtistId(String artistId);

    List<AudioFileJpaEntity> findAllByAlbumId(String albumId);

    Page<AudioFileJpaEntity> findByFileLastPlaybackDateIsNotNull(Pageable page);

    Page<AudioFileJpaEntity> findByCreatedDateAfter(LocalDateTime dateTimeAfter, Pageable page);

    long countByArtistId(String artistId);

    long countByAlbumId(String albumId);

    @Query("SELECT count(distinct a.genreId) from AudioFileJpaEntity a")
    long countDistinctGenres();

    @Query("SELECT count(distinct a.artistId) from AudioFileJpaEntity a")
    long countDistinctArtists();

    long countByFileLastPlaybackDateIsNotNull();

    long countByCreatedDateAfter(LocalDateTime dateTimeAfter);

    @Query("select a.artistId as id, a.artistName as name, count(distinct a.albumName) as albums, count(a.id) as tracks " +
            "from AudioFileJpaEntity a group by a.artistId, a.artistName")
    Stream<ArtistProjection> streamDistinctArtists();

    @Query("select a.genreId as id, a.genre as name, " +
            "count(distinct a.artistId) as artists, " +
            "count(distinct a.albumId) as albums, " +
            "count(a.id) as tracks " +
            "from AudioFileJpaEntity a group by a.genreId, a.genre")
    Stream<GenreProjection> streamDistinctGenres();

    @Query("select a.artistId as id, a.artistName as name, " +
            "count(distinct a.albumName) as albums, count(a.id) as tracks " +
            "from AudioFileJpaEntity a where a.genreId = ?1 " +
            "group by a.artistId, a.artistName")
    Stream<ArtistProjection> streamArtistsByGenreId(String genreId);

    @Query("select a.albumId as id, a.albumName as name, a.albumReleaseDate as releaseDate, " +
            "a.genreId as genreId, a.genre as genre, " +
            "a.artworkEmbedded as artworkEmbedded, a.artistId as artistId, a.artistName as artistName, " +
            "count(a.id) as tracks from AudioFileJpaEntity a where a.genreId = ?1 " +
            "group by a.albumId, a.albumName, a.albumReleaseDate, a.genreId, a.genre, a.artworkEmbedded, a.artistId, a.artistName")
    Stream<AlbumsProjection> streamAlbumsByGenreId(String genreId);

    @Query("select a.albumId as id, a.albumName as name, a.albumReleaseDate as releaseDate, " +
            "a.genreId as genreId, a.genre as genre, " +
            "a.artworkEmbedded as artworkEmbedded, a.artistId as artistId, a.artistName as artistName, " +
            "count(a.id) as tracks from AudioFileJpaEntity a where a.artistId = ?1 " +
            "group by a.albumId, a.albumName, a.albumReleaseDate, a.genreId, a.genre, a.artworkEmbedded, a.artistId, a.artistName")
    Stream<AlbumsProjection> streamAlbumsByArtistId(String artistId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update AudioFileJpaEntity a set a.playbackCount = ?2 where a.id = ?1")
    int updatePlaybackCount(long id, int count);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update AudioFileJpaEntity a set a.fileLastPlaybackDate = ?2 where a.id = ?1")
    int updateLastPlaybackDateTime(long id, Instant fileLastPlaybackDate);

    @Modifying
    long deleteByIdIsIn(Collection<Long> ids);

    @Modifying
    long deleteByLocationIsStartingWith(String basePath);

    @SuppressWarnings("SqlWithoutWhere")
    @Modifying
    @Query("delete from AudioFileJpaEntity a")
    void deleteAll();

}
