package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResTag {

    private final String id;
    private final String uri;
    private final String protocolInfo;
    private final String resolution;

    private final String duration;
    private final String bitsPerSample;
    private final String bitRate;
    private final String sampleFrequency;
    private final String size;

    private final boolean image;

}
