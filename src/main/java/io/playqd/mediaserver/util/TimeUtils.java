package io.playqd.mediaserver.util;

import java.time.Duration;
import java.util.Formatter;
import java.util.Locale;

public final class TimeUtils {

    private static final String DLNA_DURATION_FORMAT = "%01d:%02d:%06.3f";

    public static String durationToDisplayString(Duration duration) {
        var hours = duration.toHours();
        if (hours > 0) {
            return hours + " hour(s) and " + duration.toMinutesPart() + " minute(s)";
        }
        var durationInMinutes = duration.toMinutes();
        if (durationInMinutes > 0) {
            return durationInMinutes + " minutes(s) and " + duration.toSecondsPart() + " second(s)";
        }
        var durationInSeconds = duration.toSeconds();
        if (durationInSeconds > 0) {
            return durationInSeconds + " second(s)";
        }
        var durationInMillis = duration.toMillis();
        if (durationInMillis > 0) {
            return durationInMillis + " milliseconds";
        }
        return "Ohh, that was blasting fast!";
    }

    public static String durationToDlnaFormat(double duration) {
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

    private TimeUtils() {

    }
}
