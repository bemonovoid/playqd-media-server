package io.playqd.mediaserver.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter(AccessLevel.PACKAGE)
public class PlayqdLoggingProperties {

    private boolean logSoapResponse = true;

}
