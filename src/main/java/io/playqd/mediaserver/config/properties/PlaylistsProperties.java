package io.playqd.mediaserver.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Getter
@Setter(AccessLevel.PACKAGE)
public class PlaylistsProperties {

    private ImportedPlaylistsProperties imported = new ImportedPlaylistsProperties();
}
