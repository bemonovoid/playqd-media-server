package io.playqd.mediaserver.model.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public class ArtistMetadata extends MediaMetadata {

    static final String TYPE_NAME = "artist";

    public ArtistMetadata() {
        setType(TYPE_NAME);
    }

    /**
     * Whether an artist is a person, a group, or something else. Example: "Group", "Person"
     */
    private String artistType;

    /**
     * Country code. Example: "US", "UA", "FR"
     */
    private String country;

    private ArtistBio bio;

    private Set<String> genres;

    private Set<AlbumMetadata> albums;

    @JsonIgnore
    public final boolean isArtist() {
        return true;
    }

    @JsonIgnore
    public final ArtistMetadata getAsArtist() {
        return this;
    }

}
