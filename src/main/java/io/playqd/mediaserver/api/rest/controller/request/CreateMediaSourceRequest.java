package io.playqd.mediaserver.api.rest.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter(AccessLevel.PACKAGE)
public class CreateMediaSourceRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String path;

    private boolean autoScanOnCreate;

    private boolean autoScanOnStartUp;

    private Set<String> ignoredDirectories;

}
