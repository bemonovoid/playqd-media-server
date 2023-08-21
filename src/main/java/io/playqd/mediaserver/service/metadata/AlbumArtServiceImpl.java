package io.playqd.mediaserver.service.metadata;

import io.playqd.mediaserver.api.rest.controller.RestControllerApiBasePath;
import io.playqd.mediaserver.config.cache.CacheNames;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.model.FileUtils;
import io.playqd.mediaserver.model.SupportedImageFiles;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.util.MimeTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
class AlbumArtServiceImpl implements AlbumArtService {

    private final String hostAddress;
    private final AudioFileDao audioFileDao;

    public AlbumArtServiceImpl(AudioFileDao audioFileDao, PlayqdProperties playqdProperties) {
        this.audioFileDao = audioFileDao;
        this.hostAddress = playqdProperties.buildHostAddress();
    }

    @Override
    @Cacheable(cacheNames = CacheNames.ALBUM_ART_BY_ALBUM_ID, unless="#result == null")
    public Optional<AlbumArt> get(String albumId) {
        return get(audioFileDao.getFirstAudioFileByAlbumId(albumId));
    }

    @Override
    @Cacheable(cacheNames = CacheNames.ALBUM_ART_BY_ALBUM_ID, key = "#albumId", unless="#result == null")
    public Optional<AlbumArt> get(String albumId, String albumFolderImageFileName) {
        var audioFile = audioFileDao.getFirstAudioFileByAlbumId(albumId);
        var albumFolder = audioFile.path().getParent();
        try (Stream<Path> albumFolderFilesStream = Files.list(albumFolder)) {
            return albumFolderFilesStream
                    .filter(path -> path.endsWith(albumFolderImageFileName))
                    .findFirst()
                    .map(path -> createAlbumArtFromAlbumFolderPath(path, audioFile));
        } catch (IOException e) {
            log.error("Failed to read album folder image file: {}", albumFolderImageFileName, e);
            return Optional.empty();
        }
    }

    @Override
    @Cacheable(cacheNames = CacheNames.ALBUM_ART_BY_ALBUM_ID, key = "#audioFile.albumId", unless="#result == null")
    public Optional<AlbumArt> get(AudioFile audioFile) {
        return getEmbedded(audioFile).or(() -> getFromAlbumDir(audioFile));
    }

    private Optional<AlbumArt> getEmbedded(AudioFile audioFile) {
        try {

            log.info("Getting album art from audio file metadata for '{} - {}'",
                    audioFile.artistName(), audioFile.albumName());

            var jTaggerAudioFile = AudioFileIO.read(audioFile.path().toFile());

            var artwork = jTaggerAudioFile.getTag().getFirstArtwork();

            if (artwork == null) {
                return Optional.empty();
            }

            if (artwork.getBinaryData() == null || artwork.getBinaryData().length == 0) {
                log.warn("Album art in audio file metadata wasn't found.");
                return Optional.empty();
            }

            var id = new AlbumArtId.AlbumId(audioFile.albumId());
            var uri = buildAlbumArtUri(id.get());
            var binary = artwork.getBinaryData();
            var mimeType = MimeTypeUtil.detect(binary);
            var metadata = new AlbumArtMetadata(
                    binary.length, mimeType, new SizeHeightWidth(artwork.getWidth(), artwork.getHeight()));

            log.info("Album art was found in audio file metadata.");

            return Optional.of(new AlbumArt(id, uri, metadata, () -> binary));
        } catch (Exception e) {
            log.error("Failed to read audio file metadata at: {}", audioFile.path(), e);
            return Optional.empty();
        }
    }

    private Optional<AlbumArt> getFromAlbumDir(AudioFile audioFile) {
        var location = audioFile.path();
        var albumFolder = location.getParent();

        log.info("Getting album art from album folder for '{} - {}' in {}",
                audioFile.artistName(), audioFile.albumName(), albumFolder);

        try (Stream<Path> albumFolderFilesStream = Files.list(albumFolder)) {
            var mayBeAlbumArt =  albumFolderFilesStream
                    .filter(Files::isRegularFile)
                    .filter(path -> SupportedImageFiles.isAlbumArtFile(path, audioFile))
                    .findFirst()
                    .map(path -> createAlbumArtFromAlbumFolderPath(path, audioFile));
            mayBeAlbumArt.ifPresentOrElse(
                    albumArt -> log.info("Album art was found in album folder: '{}'", albumArt.id().get()),
                    () -> log.warn("Album art wasn't found"));
            return mayBeAlbumArt;
        } catch (Exception e) {
            log.error("Album artwork search failed at externalUrl: {}. {}", location, e.getMessage());
            return Optional.empty();
        }
    }

    private AlbumArt createAlbumArtFromAlbumFolderPath(Path path, AudioFile audioFile) {
        var id = new AlbumArtId.AlbumFolderImageFileName(audioFile.albumId(), path.getFileName().toString());
        var uri = buildAlbumArtUri(id.get());
        var mimeType = MimeTypeUtil.detect(path.toString());
        var metadata = new AlbumArtMetadata(FileUtils.getFileSize(path), mimeType, SizeHeightWidth.none());
        return new AlbumArt(id, uri, metadata, BinaryData.fromLocation(path.toString()));
    }

    private String buildAlbumArtUri(String id) {
        return String.format("http://%s%s/%s", hostAddress, RestControllerApiBasePath.ALBUM_ART, id);
    }

}