package io.playqd.mediaserver.model;

public record Album(String id,
                    String name,
                    String releaseDate,
                    String genreId,
                    String genre,
                    String artistId,
                    String artistName,
                    boolean artworkEmbedded,
                    int tracksCount) {
}
