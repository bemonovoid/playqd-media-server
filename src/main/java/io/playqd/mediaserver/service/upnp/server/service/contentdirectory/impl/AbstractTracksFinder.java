package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.model.AudioFile;
import io.playqd.mediaserver.model.BrowsableObject;
import io.playqd.mediaserver.model.FileUtils;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.service.metadata.AlbumArt;
import io.playqd.mediaserver.service.metadata.AlbumArtService;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.*;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.support.model.ProtocolInfo;
import org.jupnp.util.MimeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
abstract class AbstractTracksFinder extends BrowsableObjectBuilder implements BrowsableObjectFinder {

    protected final AudioFileDao audioFileDao;
    protected final AlbumArtService albumArtService;
    protected final PlayqdProperties playqdProperties;

    AbstractTracksFinder(AudioFileDao audioFileDao,
                         AlbumArtService albumArtService,
                         PlayqdProperties playqdProperties) {
        this.audioFileDao = audioFileDao;
        this.albumArtService = albumArtService;
        this.playqdProperties = playqdProperties;
    }

    protected abstract long countTotal(BrowseContext context);

    protected abstract String buildItemObjectId(AudioFile audioFile);

    protected abstract Page<AudioFile> findAudioFiles(BrowseContext context, Pageable pageable);

    protected int calculateRequestedCount(BrowseContext context) {
        return context.getRequest().getRequestedCount();
    }

    protected Page<AudioFile> findAudioFiles(BrowseContext context) {
        return findAudioFiles(context, Pageable.unpaged());
    }

    protected String getDcTitle(BrowseContext context, AudioFile audioFile) {
        var dcTitle = audioFile.trackName();
        var validateXml = context.getHeader(BrowseContext.HEADER_INVALID_XML_CHAR_VALIDATION_ENABLED, false);
        if (validateXml) {
            dcTitle = getValidatedDcTitle(audioFile);
        }
        return dcTitle;
    }

    @Override
    public final BrowseResult find(BrowseContext context) {
        var browseRequest = context.getRequest();
        var requestedStartIdx = browseRequest.getStartingIndex();
        if (requestedStartIdx > 0) {
            return findInRange(context);
        }
        var requestedCount = calculateRequestedCount(context);
        log.info("Requesting first {} audio files from starting index: 0", requestedCount);
        var audioFiles = findAudioFiles(context, Pageable.ofSize(requestedCount));
        var result = audioFiles.stream()
                .map(audioFile -> buildItemObject(context, audioFile))
                .toList();
        return new BrowseResult(audioFiles.getTotalElements(), result.size(), result);
    }

    private BrowseResult findInRange(BrowseContext context) {
        long totalCount = countTotal(context);
        var requestedStartIdx = context.getRequest().getStartingIndex();
        if (requestedStartIdx > totalCount) {
            log.warn("Requested starting index ({}) is greater then available tracks.", requestedStartIdx);
            return BrowseResult.empty();
        }
        var requestedCount = calculateRequestedCount(context);
        log.info("Requesting first {} audio files from starting index: {}.", requestedCount, requestedStartIdx);
        var audioFiles = findAudioFiles(context);
        var result = audioFiles.getContent().stream()
                .skip(requestedStartIdx)
                .map(audioFile -> buildItemObject(context, audioFile))
                .limit(requestedCount)
                .toList();
        return new BrowseResult(audioFiles.getTotalElements(), result.size(), result);
    }

    private BrowsableObject buildItemObject(BrowseContext context, AudioFile audioFile) {
        var objectId = buildItemObjectId(audioFile);
        var mayBeAlbumArt = albumArtService.get(audioFile);
        return BrowsableObjectImpl.builder()
                .objectId(objectId)
                .parentObjectId(context.getObjectId())
                .dc(buildDcTagValues(context, audioFile))
                .upnp(buildUpnpTagValues(audioFile, mayBeAlbumArt.orElse(null)))
                .resources(buildResources(audioFile, mayBeAlbumArt.orElse(null)))
                .build();
    }

    private DcTagValues buildDcTagValues(BrowseContext context, AudioFile audioFile) {
        return DcTagValues.builder()
                .title(getDcTitle(context, audioFile))
                .creator(audioFile.artistName())
                .contributor(audioFile.artistName())
                .build();
    }

    private String getValidatedDcTitle(AudioFile audioFile) {
        var possibleDcTitles = new LinkedList<Supplier<String>>();
        possibleDcTitles.add(audioFile::trackName);
        possibleDcTitles.add(() ->
                FileUtils.getFileNameWithoutExtension(audioFile.path().getFileName().toString()));
        return BrowsableObjectValidations.getFirstValidValue(possibleDcTitles);
    }

    private UpnpTagValues buildUpnpTagValues(AudioFile audioFile, AlbumArt albumArt) {
        return UpnpTagValues.builder()
                .artist(audioFile.artistName())
                .album(audioFile.albumName())
                .author(audioFile.artistName())
                .producer(audioFile.artistName())
                .genre(audioFile.genre())
                .originalTrackNumber(audioFile.trackNumber())
                .upnpClass(UpnpClass.musicTrack)
                .playbackCount(audioFile.playbackCount())
                .lastPlaybackTime(AudioFile.getLastPlaybackTimeFormatted(audioFile).orElse(null))
                .albumArtURI(albumArt != null ? albumArt.uri() : null)
                .build();
    }

    private List<ResTag> buildResources(AudioFile audioFile, AlbumArt albumArt) {
        var audioFileResource = buildAudioFileResource(audioFile);
        var albumArtResources = buildAlbumArtRes(albumArt);
        var result = new ArrayList<ResTag>(albumArtResources.size() + 1);
        result.add(audioFileResource);
        result.addAll(albumArtResources);
        return result;
    }

    private ResTag buildAudioFileResource(AudioFile audioFile) {
        return ResTag.builder()
                .id(Long.toString(audioFile.id()))
                .uri(audioFile.getAudioStreamUri())
                .protocolInfo(new ProtocolInfo(MimeType.valueOf(audioFile.mimeType())).toString())
                .bitsPerSample(Integer.toString(audioFile.bitsPerSample()))
                .bitRate(audioFile.bitRate())
                .sampleFrequency(audioFile.sampleRate())
                .size(Long.toString(audioFile.size()))
                .duration(ResTag.formatDLNADuration(audioFile.preciseTrackLength()))
                .build();
    }

    private List<ResTag> buildAlbumArtRes(AlbumArt albumArt) {
        if (albumArt == null) {
            return Collections.emptyList();
        }
        try {
            var mimeType = MimeType.valueOf(albumArt.metadata().mimeType());
            return List.of(buildResTag(albumArt, mimeType));
        } catch (IllegalArgumentException e) {
            log.error("Unexpected error when reading the mime type of album art with id: {}", albumArt.id().get(), e);
            return Collections.emptyList();
        }
    }

    private static ResTag buildResTag(AlbumArt albumArt, MimeType mimeType) {
        return ResTag.builder()
                .id(albumArt.id().get())
                .uri(albumArt.uri())
                .protocolInfo(buildImageProtocolInfo(albumArt, mimeType))
                .size(String.valueOf(albumArt.metadata().fileSize()))
                .image(true)
                .build();
    }

    private static String buildImageProtocolInfo(AlbumArt albumArt, MimeType mimeType) {
        return String.format("http-get:*:%s:DLNA.ORG_PN=JPEG_TN;DLNA.ORG_FLAGS=00900000000000000000000000000000",
                mimeType.toStringNoParameters());
    }

}
