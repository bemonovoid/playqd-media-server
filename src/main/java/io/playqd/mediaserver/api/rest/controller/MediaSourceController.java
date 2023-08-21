package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.service.mediasource.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/sources")
class MediaSourceController {

    private final MediaSourceService mediaSourceService;
    private final MediaSourceActionVisitor mediaSourceActionVisitor;

    MediaSourceController(MediaSourceService mediaSourceService, MediaSourceActionVisitor mediaSourceActionVisitor) {
        this.mediaSourceService = mediaSourceService;
        this.mediaSourceActionVisitor = mediaSourceActionVisitor;
    }

    @GetMapping("/{id}")
    MediaSource get(@PathVariable long id) {
        return mediaSourceService.get(id);
    }

    @GetMapping
    List<MediaSource> getAll() {
        return mediaSourceService.getAll();
    }

    @GetMapping("/{id}/info")
    MediaSourceContentInfo info(@PathVariable long id) {
        return mediaSourceService.info(id);
    }

    @PostMapping("/actions")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void scan(@RequestBody MediaSourceAction action) {
        action.accept(mediaSourceActionVisitor);
    }
}
