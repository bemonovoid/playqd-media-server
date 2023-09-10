package io.playqd.mediaserver.service.upnp.service.contentdirectory;

import jakarta.annotation.Nonnull;

import java.util.function.Function;

public enum ObjectIdPattern {

    ARTIST_ALBUMS_PATH(Patterns.ART_ALB_PATH_VALUE, 1,
            args -> Patterns.ART_ALB_PATH_VALUE.replaceFirst("\\{artistId}", args[0])),

    ARTIST_TRACKS_PATH(Patterns.ART_TRS_PATH_VALUE, 1,
            args -> Patterns.ART_TRS_PATH_VALUE.replaceFirst("\\{artistId}", args[0])),

    ARTIST_ALBUM_TRACKS_PATH(Patterns.ART_ALB_TRS_PATH_VALUE, 2, args -> {
        var result = Patterns.ART_ALB_TRS_PATH_VALUE.replaceFirst("\\{artistId}", args[0]);
        return result.replaceFirst("\\{albumId}", args[1]);
    }),

    ARTIST_ALBUM_TRACK_PATH(Patterns.ART_ALB_TR_PATH_VALUE, 3, args -> {
        var result = Patterns.ART_ALB_TR_PATH_VALUE.replaceFirst("\\{artistId}", args[0]);
        result = result.replaceFirst("\\{albumId}", args[1]);
        return result.replaceFirst("\\{trackId}", args[2]);
    }),

    ARTIST_TRACK_PATH(Patterns.ART_TR_PATH_VALUE, 2, args -> {
        var result = Patterns.ART_TR_PATH_VALUE.replaceFirst("\\{artistId}", args[0]);
        return result.replaceFirst("\\{trackId}", args[1]);
    }),

    GENRE_ARTISTS_PATH(Patterns.GNR_ART_PATH_VALUE, 1,
            args -> Patterns.GNR_ART_PATH_VALUE.replaceFirst("\\{genreId}", args[0])),

    GENRE_ALBUMS_PATH(Patterns.GNR_ALB_PATH_VALUE, 1,
            args -> Patterns.GNR_ALB_PATH_VALUE.replaceFirst("\\{genreId}", args[0])),

    TRACK_PATH(Patterns.TRACK_PATH_VALUE, 1, arg -> Patterns.TRACK_PATH_VALUE.replaceFirst("\\{trackId}", arg[0])),

    PLAYLIST_PATH(Patterns.PLAYLIST_PATH, 1, arg -> Patterns.PLAYLIST_PATH.replaceFirst("\\{playlistId}", arg[0]));

    private final String pattern;
    private final int requiredArgs;
    private final Function<String[], String> compiler;

    ObjectIdPattern(String pattern, int requiredArgs, Function<String[], String> compiler) {
        this.pattern = pattern;
        this.requiredArgs = requiredArgs;
        this.compiler = compiler;
    }

    String getPattern() {
        return this.pattern;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    public String compile(@Nonnull String... args) {
        if (requiredArgs != args.length) {
            throw new IllegalArgumentException(String.format(
                    "Unexpected number of required args. Expected: %s, was: %s", requiredArgs, args.length));
        }
        return compiler.apply(args);
    }

    private static class Patterns {

        private static final String ART_ALB_PATH_VALUE = "artists/{artistId}/albums";
        private static final String ART_TRS_PATH_VALUE = "artists/{artistId}/tracks";
        private static final String ART_ALB_TRS_PATH_VALUE = "artists/{artistId}/albums/{albumId}/tracks";
        private static final String ART_ALB_TR_PATH_VALUE = "artists/{artistId}/albums/{albumId}/tracks/{trackId}";
        private static final String ART_TR_PATH_VALUE = "artists/{artistId}/tracks/{trackId}";
        private static final String GNR_ART_PATH_VALUE = "genres/{genreId}/artists";
        private static final String GNR_ALB_PATH_VALUE = "genres/{genreId}/albums";
        private static final String TRACK_PATH_VALUE = "tracks/{trackId}";
        private static final String PLAYLIST_PATH = "playlists/{playlistId}";
    }
}
