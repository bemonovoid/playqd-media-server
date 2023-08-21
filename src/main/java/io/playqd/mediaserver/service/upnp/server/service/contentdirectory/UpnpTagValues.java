package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpnpTagValues {

    private final String artist;
    private final String album;
    private final String genre;
    private final String author;
    private final String producer;
    private final String originalTrackNumber;
    private final UpnpClass upnpClass;
    private final String albumArtURI;
    private final int playbackCount;
    private final String lastPlaybackTime;
}
