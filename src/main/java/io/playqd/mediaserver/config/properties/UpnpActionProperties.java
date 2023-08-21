package io.playqd.mediaserver.config.properties;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter(AccessLevel.PACKAGE)
@Validated
public class UpnpActionProperties {

    private BrowseActionProperties browse = new BrowseActionProperties();

    @Getter
    @Setter(AccessLevel.PACKAGE)
    @Validated
    public static class BrowseActionProperties {

        @PositiveOrZero
        private int maxDisplayedRecentlyAdded = 100;

        @PositiveOrZero
        private int maxDisplayedRecentlyPlayed = 100;

        @PositiveOrZero
        private int maxDisplayedMostPlayed = 100;
    }
}
