package io.playqd.mediaserver.persistence.jpa.entity;

import io.playqd.mediaserver.model.AudioFile;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AudioFileEntityEventListener.class)
@Entity
@Table(name = AudioFileJpaEntity.TABLE_NAME, indexes = {
        @Index(name = "name_idx", columnList = AudioFileJpaEntity.COL_NAME),
        @Index(name = "artist_id_idx", columnList = AudioFileJpaEntity.COL_ARTIST_ID),
        @Index(name = "artist_name_idx", columnList = AudioFileJpaEntity.COL_ARTIST_NAME),
        @Index(name = "album_id_idx", columnList = AudioFileJpaEntity.COL_ALBUM_ID),
        @Index(name = "album_name_idx", columnList = AudioFileJpaEntity.COL_ALBUM_NAME),
        @Index(name = "genre_id_idx", columnList = AudioFileJpaEntity.COL_GENRE_ID),
        @Index(name = "genre_name_idx", columnList = AudioFileJpaEntity.COL_GENRE),
        @Index(name = "track_id_idx", columnList = AudioFileJpaEntity.COL_TRACK_ID),
        @Index(name = "track_name_idx", columnList = AudioFileJpaEntity.COL_TRACK_NAME)
})
public class AudioFileJpaEntity extends PersistableAuditableEntity implements AudioFile {

    public static final String TABLE_NAME = "audio_file";

    public static final String COL_ARTIST_ID = "artist_id";
    public static final String COL_ALBUM_ID = "album_id";
    public static final String COL_TRACK_ID = "track_id";
    public static final String COL_GENRE_ID = "genre_id";
    public static final String COL_NAME = "name";
    public static final String COL_SIZE = "size";
    public static final String COL_LOCATION = "location";
    public static final String COL_EXTENSION = "extension";
    public static final String COL_MIME_TYPE = "mime_type";
    public static final String COL_FILE_PLAYBACK_COUNT = "file_playback_count";
    public static final String COL_FILE_LAST_PLAYBACK_DATE = "file_last_playback_date";
    public static final String COL_FILE_LAST_SCANNED_DATE = "file_last_scanned_date";
    public static final String COL_FILE_LAST_MODIFIED_DATE = "file_last_modified_date";

    public static final String COL_FORMAT = "format";
    public static final String COL_BIT_RATE = "bit_rate";
    public static final String COL_LOSSLESS = "lossless";
    public static final String COL_CHANNELS = "channels";
    public static final String COL_SAMPLE_RATE = "sample_rate";
    public static final String COL_ENCODING_TYPE = "encoding_type";
    public static final String COL_BITS_PER_SAMPLE = "bits_per_sample";

    public static final String COL_ARTIST_NAME = "artist_name";
    public static final String COL_ARTIST_COUNTRY = "artist_country";

    public static final String COL_ALBUM_NAME = "album_name";
    public static final String COL_ALBUM_RELEASE_DATE = "album_release_date";

    public static final String COL_TRACK_NAME = "track_name";
    public static final String COL_TRACK_NUMBER = "track_number";
    public static final String COL_TRACK_LENGTH = "track_length";
    public static final String COL_PRECISE_TRACK_LENGTH = "precise_track_length";

    public static final String COL_COMMENT = "comment";
    public static final String COL_LYRICS = "lyrics";
    public static final String COL_GENRE = "genre";

    public static final String COL_ARTWORK_EMBEDDED = "artwork_embedded";

    public static final String COL_MB_TRACK_ID = "mb_track_id";
    public static final String COL_MB_ARTIST_ID = "mb_artist_id";
    public static final String COL_MB_RELEASE_TYPE = "mb_release_type";
    public static final String COL_MB_RELEASE_GROUP_ID = "mb_release_group_id";

    public static final String FLD_FILE_LAST_PLAYBACK_DATE = "fileLastPlaybackDate";
    public static final String FLD_FILE_PLAYBACK_COUNT = "playbackCount";

    public static final Set<String> METADATA_UPDATABLE_COLUMNS = Set.of(
            COL_NAME, COL_SIZE, COL_EXTENSION, COL_FILE_LAST_MODIFIED_DATE, COL_ARTIST_NAME, COL_ARTIST_COUNTRY,
            COL_ALBUM_NAME, COL_ALBUM_RELEASE_DATE, COL_TRACK_NAME, COL_TRACK_NUMBER,
            COL_ARTWORK_EMBEDDED, COL_MB_ARTIST_ID, COL_MB_RELEASE_GROUP_ID, COL_MB_RELEASE_TYPE,
            COL_COMMENT, COL_LYRICS, COL_GENRE);

    @Column(name = COL_ARTIST_ID, nullable = false)
    private String artistId;

    @Column(name = COL_ALBUM_ID, nullable = false)
    private String albumId;

    @Column(name = COL_TRACK_ID, nullable = false)
    private String trackId;

    @Column(name = COL_GENRE_ID, nullable = false)
    private String genreId;

    @Column(name = COL_NAME, length = 512)
    private String name;

    @Column(name = COL_SIZE)
    private long size;

    @Column(name = COL_LOCATION, columnDefinition = "LONGTEXT")
    private String location;

    @Column(name = COL_EXTENSION)
    private String extension;

    @Column(name = COL_MIME_TYPE)
    private String mimeType;

    @Column(name = COL_FILE_PLAYBACK_COUNT, nullable = false)
    @ColumnDefault("0")
    private int playbackCount;

    /**
     * FLD_FILE_LAST_PLAYBACK_DATE
     */
    @Column(name = COL_FILE_LAST_PLAYBACK_DATE)
    private Instant fileLastPlaybackDate;

    @Column(name = COL_FILE_LAST_SCANNED_DATE)
    private Instant fileLastScannedDate;

    @Column(name = COL_FILE_LAST_MODIFIED_DATE)
    private Instant fileLastModifiedDate;

    @Column(name = COL_FORMAT, length = 100)
    private String format;

    @Column(name = COL_BIT_RATE, length = 100)
    private String bitRate;

    @Column(name = COL_CHANNELS, length = 100)
    private String channels;

    @Column(name = COL_LOSSLESS)
    private boolean lossless;

    @Column(name = COL_SAMPLE_RATE, length = 100)
    private String sampleRate;

    @Column(name = COL_ENCODING_TYPE, length = 100)
    private String encodingType;

    @Column(name = COL_BITS_PER_SAMPLE)
    private int bitsPerSample;

    @Column(name = COL_ARTIST_NAME, length = 512)
    private String artistName;

    @Column(name = COL_ARTIST_COUNTRY)
    private String artistCountry;

    @Column(name = COL_ALBUM_NAME, length = 512)
    private String albumName;

    @Column(name = COL_ALBUM_RELEASE_DATE)
    private String albumReleaseDate;

    @Column(name = COL_TRACK_NAME, length = 512)
    private String trackName;

    @Column(name = COL_TRACK_NUMBER)
    private String trackNumber;

    @Column(name = COL_TRACK_LENGTH)
    private int trackLength;

    @Column(name = COL_PRECISE_TRACK_LENGTH)
    private double preciseTrackLength;

    @Column(name = COL_COMMENT, columnDefinition = "LONGTEXT")
    @Basic(fetch = FetchType.LAZY)
    private String comment;

    @Column(name = COL_LYRICS, columnDefinition = "LONGTEXT")
    @Basic(fetch = FetchType.LAZY)
    private String lyrics;

    @Column(name = COL_GENRE)
    private String genre;

    @Column(name = COL_ARTWORK_EMBEDDED)
    private boolean artworkEmbedded;

    @Column(name = COL_MB_TRACK_ID)
    private String mbTrackId;

    @Column(name = COL_MB_ARTIST_ID)
    private String mbArtistId;

    @Column(name = COL_MB_RELEASE_TYPE)
    private String mbReleaseType;

    @Column(name = COL_MB_RELEASE_GROUP_ID)
    private String mbReleaseGroupId;

    @Transient
    private String audioFileStreamUri;

    @Override
    public Long id() {
        return getId();
    }

    @Override
    public final String getAudioStreamUri() {
        return audioFileStreamUri;
    }

    void withAudioFilesStreamUri(String uri) {
        this.audioFileStreamUri = uri;
    }
}
