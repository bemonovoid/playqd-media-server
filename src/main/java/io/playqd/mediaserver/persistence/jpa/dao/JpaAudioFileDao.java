package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.model.Album;
import io.playqd.mediaserver.model.Artist;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.model.Genre;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.entity.AudioFileJpaEntity;
import io.playqd.mediaserver.persistence.jpa.entity.AudioFileSourceAuditLogJpaEntity;
import io.playqd.mediaserver.persistence.jpa.entity.PersistableAuditableEntity;
import io.playqd.mediaserver.persistence.jpa.repository.AudioFileRepository;
import io.playqd.mediaserver.persistence.jpa.repository.AudioFileAuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Component
class JpaAudioFileDao implements AudioFileDao {

    private final JdbcTemplate jdbcTemplate;
    private final AudioFileRepository audioFileRepository;
    private final AudioFileAuditLogRepository audioFileAuditLogRepository;

    JpaAudioFileDao(JdbcTemplate jdbcTemplate,
                    AudioFileRepository audioFileRepository,
                    AudioFileAuditLogRepository audioFileAuditLogRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.audioFileRepository = audioFileRepository;
        this.audioFileAuditLogRepository = audioFileAuditLogRepository;
    }

    @Override
    public long countGenres() {
        return audioFileRepository.countDistinctGenres();
    }

    @Override
    public long countArtists() {
        return audioFileRepository.countDistinctArtists();
    }

    @Override
    public long countPlayed() {
        return audioFileRepository.countByFileLastPlaybackDateIsNotNull();
    }

    @Override
    public long countRecentlyAdded() {
        return resolveDateAfter().map(audioFileRepository::countByCreatedDateAfter).orElse(0L);
    }

    @Override
    public long countAlbumAudioFiles(String albumId) {
        return audioFileRepository.countByAlbumId(albumId);
    }

    @Override
    public long countArtistAudioFiles(String artistId) {
        return audioFileRepository.countByArtistId(artistId);
    }

    @Override
    public AudioFile getAudioFile(long id) {
        return audioFileRepository.get(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllGenres() {
        return audioFileRepository.streamDistinctGenres()
                .map(prj -> new Genre(prj.getId(), prj.getName(), prj.getArtists(), prj.getAlbums(), prj.getTracks()))
                .sorted()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> getAllArtists() {
        return audioFileRepository.streamDistinctArtists()
                .map(prj -> new Artist(prj.getId(), prj.getName(), prj.getAlbums(), prj.getTracks()))
                .distinct() //TODO ??? remove?
                .sorted()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artist> getGenreArtists(String genreId) {
        return audioFileRepository.streamArtistsByGenreId(genreId)
                .map(prj -> new Artist(prj.getId(), prj.getName(), prj.getAlbums(), prj.getTracks()))
                .sorted()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> getGenreAlbums(String genreId) {
        return audioFileRepository.streamAlbumsByGenreId(genreId).map(JpaAudioFileDao::fromProjection).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> getArtistAlbums(String artistId) {
        return audioFileRepository.streamAlbumsByArtistId(artistId).map(JpaAudioFileDao::fromProjection).toList();
    }

    @Override
    public AudioFile getFirstAudioFileByAlbumId(String albumId) {
        return audioFileRepository.findFirstByAlbumId(albumId);
    }

    @Override
    public List<? extends AudioFile> getAllAudioFiles() {
        return audioFileRepository.findAll(Pageable.unpaged()).getContent();
    }

    @Override
    public List<AudioFile> getAudioFilesByLocationIn(Collection<String> locations) {
        return Collections.unmodifiableList(audioFileRepository.findAllByLocationIn(locations));
    }

    @Override
    public List<AudioFile> getAudioFilesByArtistId(String artistId) {
        return Collections.unmodifiableList(audioFileRepository.findAllByArtistId(artistId));
    }

    @Override
    public List<AudioFile> getAudioFilesByAlbumId(String albumId) {
        return Collections.unmodifiableList(audioFileRepository.findAllByAlbumId(albumId));
    }

    @Override
    public Page<AudioFile> getRecentlyAdded(Pageable pageable) {
        return resolveDateAfter()
                .map(dateAfter -> audioFileRepository.findByCreatedDateAfter(dateAfter, pageable)
                        .map(entity -> (AudioFile) entity))
                .orElse(Page.empty());
    }

    @Override
    public Page<AudioFile> getPlayed(Pageable pageable) {
        var queryPageable = pageable;
        var sort = pageable.getSort();
        if (pageable.getSort().isUnsorted()) {
            sort = Sort.by(AudioFileJpaEntity.FLD_FILE_LAST_PLAYBACK_DATE).descending();
            queryPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return audioFileRepository.findByFileLastPlaybackDateIsNotNull(queryPageable).map(entity -> entity);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Stream<T> streamByLocationStartsWith(Path basePath, Class<T> type) {
        return audioFileRepository.findByLocationIsStartingWith(basePath.toString(), type);
    }

    @Override
    public int insertAll(List<Map<String, ?>> audioFilesData) {
        var sqlParameterSources = audioFilesData.stream()
                .map(MapSqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        var jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(AudioFileJpaEntity.TABLE_NAME)
                .usingGeneratedKeyColumns(PersistableAuditableEntity.COL_PK_ID);

        return jdbcInsert.executeBatch(sqlParameterSources).length;
    }

    @Override
    @Transactional
    public void setNewLastRecentlyAddedDate(LocalDateTime lastRecentlyAddedDateTime) {
        var entity = audioFileAuditLogRepository.getOne().orElseGet(AudioFileSourceAuditLogJpaEntity::new);
        entity.setLastAddedDate(lastRecentlyAddedDateTime);
        audioFileAuditLogRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void updateAudioFileLastPlaybackDate(long audioFileId) {
        var audioFile = getAudioFile(audioFileId);
        audioFileRepository.updateLastPlaybackDateTime(audioFileId, Instant.now());
        audioFileRepository.updatePlaybackCount(audioFileId, audioFile.playbackCount() + 1);
    }

    @Override
    public int updateAll(Map<Long, Map<String, ?>> audioFilesData) {
        if (CollectionUtils.isEmpty(audioFilesData)) {
            return 0;
        }

        var updatesCount = 0;
        var setters = new StringJoiner(",");

        Map<String, Object> updates = new LinkedHashMap<>(AudioFileJpaEntity.METADATA_UPDATABLE_COLUMNS.size());

        for (Map.Entry<Long, Map<String, ?>> updatedMetadata : audioFilesData.entrySet()) {
            updatedMetadata.getValue().entrySet().stream()
                    .filter(e -> AudioFileJpaEntity.METADATA_UPDATABLE_COLUMNS.contains(e.getKey()))
                    .forEach(entry -> updates.put(entry.getKey(), entry.getValue()));
            for (Map.Entry<String, ?> entry : updates.entrySet()) {
                setters.add(entry.getKey() + "=?");
            }

            var sql = String.format("UPDATE %s SET %s WHERE %s=?",
                    AudioFileJpaEntity.TABLE_NAME, setters, AudioFileJpaEntity.COL_PK_ID);

            jdbcTemplate.update(sql, ps -> {
                var i = 1;
                for (Map.Entry<String, ?> entry : updates.entrySet()) {
                    ps.setObject(i, entry.getValue());
                    i++;
                }
                ps.setLong(i, updatedMetadata.getKey());
            });

            updatesCount++;

        }
        return updatesCount;
    }

    @Override
    @Transactional
    public long deleteAllByIds(List<Long> ids) {
        return audioFileRepository.deleteByIdIsIn(ids);
    }

    @Override
    @Transactional
    public long deleteAllByLocationsStartsWith(Path path) {
        return audioFileRepository.deleteByLocationIsStartingWith(path.toString());
    }

    private Optional<LocalDateTime> resolveDateAfter() {
        return audioFileAuditLogRepository.getOne().map(entity -> entity.getLastAddedDate().minusHours(1));
    }

    private static Album fromProjection(AlbumsProjection projection) {
        return new Album(
                projection.getId(),
                projection.getName(),
                projection.getReleaseDate(),
                projection.getGenreId(),
                projection.getGenre(),
                projection.getArtistId(),
                projection.getArtistName(),
                projection.getArtworkEmbedded(),
                projection.getTracks());
    }
}
