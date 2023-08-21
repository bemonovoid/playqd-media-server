package io.playqd.mediaserver.model;

public record Genre(String id, String name, int artistCount, int albumCount, int trackCount)
        implements Comparable<Genre> {

    @Override
    public int compareTo(Genre that) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.name, that.name);
    }
}
