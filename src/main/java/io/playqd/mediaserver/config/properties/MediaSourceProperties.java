package io.playqd.mediaserver.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Getter
@Setter(AccessLevel.PACKAGE)
@Validated
public class MediaSourceProperties {

    @NotBlank
    private String name;

    @NotBlank
    private String dir;

    private boolean scanOnStart;

    private boolean watchable;

    private Set<String> ignoreDirs;

}
