package io.playqd.mediaserver.persistence.jpa.dao;

public interface ArtistProjection {

    String getId();

    String getName();

    int getAlbums();

    int getTracks();
}
