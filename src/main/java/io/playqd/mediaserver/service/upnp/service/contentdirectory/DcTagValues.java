package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DcTagValues {

    /**
     * <dc:title>
     */
    private final String title;

    /**
     * <dc:description>
     */
    private final String description;

    /**
     * <dc:creator>
     */
    private final String creator;

    /**
     * <dc:contributor>
     */
    private final String contributor;
}
