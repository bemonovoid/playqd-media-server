package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.service.upnp.service.ActionContext;

import java.util.HashMap;
import java.util.Map;

public class BrowseContext extends ActionContext<Browse> {

    public static final String HEADER_ARTIST_ID = "artistId";
    public static final String HEADER_ALBUM_ID = "albumId";
    public static final String HEADER_GENRE_ID = "genreId";
    public static final String HEADER_PLAYLIST_ID = "playlistId";
    public static final String HEADER_INVALID_XML_CHAR_VALIDATION_ENABLED = "validateOnInvalidXmlCharacter";

    public static final String HEADER_OBJECT_ID_PATTERN = "objectIdPattern";

    private final Browse browse;

    public BrowseContext(Browse browse) {
        this(browse, new HashMap<>());
    }

    public BrowseContext(Browse browse, Map<String, Object> headers) {
        super(headers);
        this.browse = browse;
    }

    @Override
    public Browse getRequest() {
        return this.browse;
    }

    public String getObjectId() {
        return this.browse.getObjectID();
    }

}
