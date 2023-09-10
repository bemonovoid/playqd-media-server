package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum SystemContainerName {

    ROOT_FOLDERS("FoldersContainerObjectID", "Folders"),

    ROOT_MUSIC_LIBRARY("MusicLibraryContainerObjectID", "Music library"),

    ARTIST_ALBUM("ArtistAlbumsContainerObjectID", "Artist/Album"),

    ARTIST_TRACK("ArtistTracksContainerObjectID", "Artist/Track"),

    GENRE_ARTIST("GenreArtistsContainerObjectID", "Genre/Artist"),

    GENRE_ALBUM("GenreAlbumsContainerObjectID", "Genre/Album"),

    PLAYLISTS("PlaylistsContainerObjectID", "Playlists"),

    TRACKS_MOST_PLAYED("TracksMostPlayedContainerObjectId", "Most played"),

    TRACKS_RECENTLY_ADDED("TracksLastAddedContainerObjectID", "Recently added"),

    TRACKS_RECENTLY_PLAYED("TracksLastPlayedContainerObjectID", "Recently played");

    private final String objectId;
    private final String dcTitleName;

    private static final Set<SystemContainerName> MUSIC_LIBRARY_CHILDREN = Set.of(
            ARTIST_ALBUM, ARTIST_TRACK, GENRE_ARTIST,  GENRE_ALBUM, PLAYLISTS,
            TRACKS_MOST_PLAYED, TRACKS_RECENTLY_ADDED, TRACKS_RECENTLY_PLAYED);

    SystemContainerName(String objectId, String dcTitleName) {
        this.objectId = objectId;
        this.dcTitleName = dcTitleName;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getDcTitleName() {
        return dcTitleName;
    }

    public int getOrderId() {
        return ordinal();
    }

    public static Set<SystemContainerName> getMusicLibraryChildren() {
        return MUSIC_LIBRARY_CHILDREN;
    }

    public static Set<String> getMusicLibraryChildrenObjectIds() {
        return getMusicLibraryChildren().stream().map(SystemContainerName::getObjectId).collect(Collectors.toSet());
    }

    public static boolean isMusicLibraryChildObjectId(String objectId) {
        return getMusicLibraryChildrenObjectIds().contains(objectId);
    }

    public static Optional<SystemContainerName> getFromObjectId(String objectId) {
        return Arrays.stream(SystemContainerName.values())
                .filter(e -> e.getObjectId().equals(objectId))
                .findFirst();
    }

}
