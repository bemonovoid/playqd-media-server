package io.playqd.mediaserver.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter(AccessLevel.PACKAGE)
public class PlayqdLoggingConfig {

    private boolean logSoapResponse = true;

}
