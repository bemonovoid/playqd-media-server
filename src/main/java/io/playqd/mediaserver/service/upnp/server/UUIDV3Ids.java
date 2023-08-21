package io.playqd.mediaserver.service.upnp.server;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class UUIDV3Ids {

    private UUIDV3Ids() {

    }

    public static String create(String value) {
        return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)).toString();
    }
}
