package io.playqd.mediaserver.persistence.jpa.dao;

public interface AlbumsProjection {

    String getId();

    String getName();

    String getArtistId();

    String getArtistName();

    String getReleaseDate();

    String getGenreId();

    String getGenre();

    boolean getArtworkEmbedded();

    int getTracks();
}
