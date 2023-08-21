package io.playqd.mediaserver.model;

import java.time.Duration;

public final class TimeUtils {

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

    private TimeUtils() {

    }
}
