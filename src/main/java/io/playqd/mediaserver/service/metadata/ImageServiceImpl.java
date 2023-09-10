package io.playqd.mediaserver.service.metadata;

import io.playqd.mediaserver.config.cache.CacheNames;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.util.FileUtils;
import io.playqd.mediaserver.util.ImageUtils;
import io.playqd.mediaserver.util.SupportedImageFiles;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class ImageServiceImpl implements ImageService {

    private final String hostAddress;
    private final AudioFileDao audioFileDao;

    private static final int IMAGE_WIDTH_SMALL = 250;
    private static final int IMAGE_HEIGHT_SMALL = 250;

    public ImageServiceImpl(PlayqdProperties playqdProperties, AudioFileDao audioFileDao) {
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
        return getEmbedded(audioFile).or(() -> getFromAlbumFolder(audioFile));
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

            var imageByteArray = artwork.getBinaryData();
            var mimeType = FileUtils.detectMimeType(imageByteArray);
            var metadata = new ImageMetadata(
                    imageByteArray.length, mimeType, new SizeHeightWidth(artwork.getWidth(), artwork.getHeight()));

            log.info("Album art was found in audio file metadata.");

            return Optional.of(new AlbumArt(audioFile.albumId(),
                    createAlbumArtImageResources(audioFile.albumId(), null, imageByteArray), metadata));
        } catch (Exception e) {
            log.error("Failed to read audio file metadata at: {}", audioFile.path(), e);
            return Optional.empty();
        }
    }

    private Optional<AlbumArt> getFromAlbumFolder(AudioFile audioFile) {
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
                    albumArt -> log.info("Found album art image in album folder"),
                    () -> log.warn("Album art wasn't found"));
            return mayBeAlbumArt;
        } catch (Exception e) {
            log.error("Album artwork search failed at externalUrl: {}. {}", location, e.getMessage());
            return Optional.empty();
        }
    }

    private AlbumArt createAlbumArtFromAlbumFolderPath(Path path, AudioFile audioFile) {
        var mimeType = FileUtils.detectMimeType(path.toString());
        var metadata = new ImageMetadata(FileUtils.getFileSize(path), mimeType, SizeHeightWidth.none());
        var imageByteArray = new byte[0];
        try {
            imageByteArray = Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Failed to read album folder image file content.", e);
        }
        return new AlbumArt(audioFile.albumId(), createAlbumArtImageResources(
                audioFile.albumId(), path.getFileName().toString(), imageByteArray), metadata);
    }

    private ImageResources createAlbumArtImageResources(String albumId,
                                                        String albumFolderImageFilename,
                                                        byte[] originalData) {
        return new ImageResources(
                new ImageResource(ImageUtils.createAlbumArtResourceUri(
                        hostAddress, albumId, albumFolderImageFilename), originalData),
                Map.of(ImageSizeRequestParam.sm,
                        new ImageResource(ImageUtils.createAlbumArtResourceUri(
                                hostAddress, albumId, albumFolderImageFilename, ImageSizeRequestParam.sm),
                                ImageUtils.resize(originalData, IMAGE_WIDTH_SMALL, IMAGE_HEIGHT_SMALL))));
    }

}