package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.model.event.AudioFileByteStreamRequestedEvent;
import io.playqd.mediaserver.persistence.AudioFileDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(RestControllerApiBasePath.AUDIO_STREAM)
class AudioStreamController {

    private final AudioFileDao audioFileDao;
    private final ApplicationEventPublisher eventPublisher;

    AudioStreamController(AudioFileDao audioFileDao, ApplicationEventPublisher eventPublisher) {
        this.audioFileDao = audioFileDao;
        this.eventPublisher = eventPublisher;
    }

    /**
     * See: Spring's {@link AbstractMessageConverterMethodProcessor} (line: 186 & 194) implementation that handles byte ranges
     * @param audioFileId
     * @param httpHeaders
     * @return Audio file stream at the given byte range.
     */
    @GetMapping("/{audioFileId}")
    ResponseEntity<Resource> audioTrackStream(@PathVariable long audioFileId, @RequestHeader HttpHeaders httpHeaders) {

        var audioFile = audioFileDao.getAudioFile(audioFileId);

        log.info("\n---Processed audio streaming info---\nTrack id: {}\nRange: {}\nResource externalUrl: {}\nContent-Type: {}",
                audioFileId,
                Arrays.toString(httpHeaders.getRange().toArray()),
                audioFile.location(),
                audioFile.mimeType());

        getHttpRangeRequestIfExists(httpHeaders)
                .filter(httpRange -> httpRange.getClass().getSimpleName().equals("ByteRange"))
                // 'getRangeStart' input is ignored for ByteRange and can be anything.
                .filter(httpRange -> httpRange.getRangeStart(0) == 0)
                .ifPresent(httpRange -> {
                    eventPublisher.publishEvent(new AudioFileByteStreamRequestedEvent(audioFile.id()));
                });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, audioFile.mimeType())
                .body(new FileSystemResource(audioFile.path()));
    }

    private Optional<HttpRange> getHttpRangeRequestIfExists(HttpHeaders httpHeaders) {
        if (httpHeaders.isEmpty()) {
            return Optional.empty();
        }
        if (CollectionUtils.isEmpty(httpHeaders.getRange())) {
            return Optional.empty();
        }
        if (httpHeaders.getRange().size() > 1) {
            log.warn("'Range' header contains multiple ranges. The first range is being used.");
        }
        return Optional.of(httpHeaders.getRange().get(0));
    }

}
