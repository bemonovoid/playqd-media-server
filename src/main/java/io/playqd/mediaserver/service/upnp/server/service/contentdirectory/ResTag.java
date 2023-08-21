package io.playqd.mediaserver.service.upnp.server.service.contentdirectory;

import lombok.Builder;
import lombok.Getter;

import java.util.Formatter;
import java.util.Locale;

@Builder
@Getter
public class ResTag {

    private static final String DLNA_DURATION_FORMAT = "%01d:%02d:%06.3f";

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

    public static String formatDLNADuration(double duration) {
        double seconds;
        int hours;
        int minutes;
        if (duration < 0) {
            seconds = 0.0;
            hours = 0;
            minutes = 0;
        } else {
            seconds = duration % 60;
            hours = (int) (duration / 3600);
            minutes = ((int) (duration / 60)) % 60;
        }
        if (hours > 99999) {
            // As per DLNA standard
            hours = 99999;
        }
        StringBuilder sb = new StringBuilder();
        try (Formatter formatter = new Formatter(sb, Locale.ROOT)) {
            formatter.format(DLNA_DURATION_FORMAT, hours, minutes, seconds);
        }
        return sb.toString();
    }

}
