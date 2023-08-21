package io.playqd.mediaserver.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpnpStreamServerProperties {

    @PositiveOrZero
    private int port;

    @NotBlank
    private String host;
}
