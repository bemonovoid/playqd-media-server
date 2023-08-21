package io.playqd.mediaserver.model.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.playqd.mediaserver.model.ReleaseType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public class AlbumMetadata extends MediaMetadata {

    static final String TYPE_NAME = "album";

    public AlbumMetadata() {
        setType(TYPE_NAME);
    }

    private String artistId;

    @JsonProperty("release_type")
    private ReleaseType releaseType;

    @JsonProperty("release_date")
    private String releaseDate;

    @Override
    public final boolean isAlbum() {
        return true;
    }

    @Override
    public final AlbumMetadata getAsAlbum() {
        return this;
    }
}
