package io.playqd.mediaserver.model.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArtistMetadata.class, name = ArtistMetadata.TYPE_NAME),
        @JsonSubTypes.Type(value = AlbumMetadata.class, name = AlbumMetadata.TYPE_NAME)
})
public abstract class MediaMetadata implements Serializable {

    private String id;

    private String name;

    private MetadataProviderName provider;

    private String url;

    private List<Image> images;

    private String type;

    @JsonIgnore
    public boolean hasImages() {
        return getImages() != null && !getImages().isEmpty();
    }

    @JsonIgnore
    public boolean isArtist() {
        return false;
    }

    @JsonIgnore
    public boolean isAlbum() {
        return false;
    }

    @JsonIgnore
    public AlbumMetadata getAsAlbum() {
        return null;
    }

    @JsonIgnore
    public ArtistMetadata getAsArtist() {
        return null;
    }

}
