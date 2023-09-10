package io.playqd.mediaserver.api.rest.controller;

public final class RestApiResources {

    public static final String BROWSABLE = "/api/v1/browsable";

    public static final String AUDIO_STREAM = "/api/v1/resources/audio";

    public static final String IMAGE = "/api/v1/resources/image";

    public static final String ALBUM_ART_IMAGE = IMAGE + "/album";

    public static final String BROWSABLE_OBJECT_IMAGE = BROWSABLE + "/image";

    private RestApiResources() {

    }
}
